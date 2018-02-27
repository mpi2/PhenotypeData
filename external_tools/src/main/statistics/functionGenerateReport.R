#!/usr/bin/env Rscript

#
#
library(compiler)
setCompilerOptions(suppressAll = TRUE, optimize = 3)
enableJIT(3)



readMethodsFile <- function(fname) {
  message("Method Map:")

  flines01 <- trim(unlist(strsplit(readLines(fname), "=")))


  result01 <- list()


  for (i in seq(1, length(flines01), by = 2)) {
    result01[flines01[i]] <- flines01[i + 1]
    message(flines01[i], " = ", flines01[i + 1])

  }

  return(result01)

}

getMethod <-
function(var,
methodMap = list(
"IMPC_ABR_" = "RR",
"IMPC_ABR_0" = "FE",
"IMPC_XRY_001_003" = "FE"
)) {
  matched001 <- sapply(names(methodMap), grepl, var)

  if (sum(matched001) > 1) {
    # map to longer pattern
    #
    methodMapReduced <- methodMap[matched001]

    mappedPatternLengths <- nchar(names(methodMapReduced))

    method <-
    unlist(methodMapReduced[which(mappedPatternLengths ==  max(mappedPatternLengths))])
  } else {
    method <- unlist(methodMap[matched001])
  }

  return(method)

}

# returns string w/o leading or trailing whitespace
trim <- function (x)
gsub("^\\s+|\\s+$", "", x)

capture.stderr <- function (..., file = NULL, append = FALSE) {
  args <- substitute(list(...))[-1L]
  rval <- NULL
  closeit <- TRUE
  if (is.null(file))
  file <- textConnection("rval", "w", local = TRUE)
  else if (is.character(file))
  file <- file(file, if (append)
  "a"
  else
  "w")
  else if (inherits(file, "connection")) {
    if (!isOpen(file))
    open(file, if (append)
    "a"
    else
    "w")
    else
    closeit <- FALSE
  }
  else
  stop("'file' must be NULL, a character string or a connection")
  sink(file, type = "message")
  on.exit({
    sink(type = "message")
    if (closeit)
    close(file)
  })
  pf <- parent.frame()
  evalVis <- function(expr)
  withVisible(eval(expr, pf))

  result01 <- c()

  for (i in seq_along(args)) {
    expr <- args[[i]]
    tmp <- switch(
    mode(expr),
    expression = lapply(expr, evalVis),
    call = ,
    name = list(evalVis(expr)),
    stop("bad argument")
    )

    result01 <- c(result01, tmp)

    #for (item in tmp) if (item$visible)
    #    print(item$value)
  }
  on.exit()
  sink(type = "message")
  if (closeit)
  close(file)
  if (is.null(rval))
  invisible(list("output" = NULL, "result" = result01))
  else
  list("output" = rval, "result" = result01)
}

test002 <-
  function(infname = "MRCHarwell-HRWL_001-IMPC_CSD-MGI2164831.tsv",
           result = "result",
           methodMapfname = "functionMethodMap",
           withWeight = FALSE) {
    message(
      "test002(infname = ",
      infname,
      ", result = ",
      result,
      ", methodMapfname = ",
      methodMapfname,
      ")",
      sep = ""
    )
    
    
    message("which R=", system("which R", intern = TRUE))
    
    methodmap <- readMethodsFile(methodMapfname)
    
    
    # DO NOT RENOVE:
    #
    # THIS IMPORT FIXES THE PROBELM OF
    # Rscript NOT BEING ABLE TO PROPERLY
    # LOAD S4 METHODS, WHICH RESULTS IN
    # AN ERROR WHEN PhenList IS INVOKED
    #
    #
    require(methods)
    
    
    #
    #
    require(PhenStat)
    
    # vars
    #
    NUM_MUTANT_FEMALE_THRES = 4
    NUM_MUTANT_MALE_THRES   = 4
    
    METADATA_SEPARATOR_COLUMN = make.names("::")
    
    STATUS_FAILED = "FAILED"
    STATUS_TESTED = "TESTED"
    
    MISSING_DATA_VALUE = ""
    
    #
    # EQUATION instructs PhenStat to model in weight
    EQUATION = "withoutWeight"
    if (withWeight) {
      EQUATION = "withWeight"
    }
    
    #
    #
    c002 <- 0
    
    
    # create the result folder
    #
    dir.create(result, showWarnings = FALSE, recursive = TRUE)
    
    
    #
    # create the pdfs folder if not exists
    #
    pdfs_dir = paste(dirname(result), "/pdfs", sep = "")
    dir.create(pdfs_dir, showWarnings = FALSE, recursive = TRUE)
    basefilename = strsplit(basename(infname), ".", fixed = TRUE)[[1]][1]
    
    
    
    dataset = read.delim(
      infname,
      header = TRUE,
      sep = "\t",
      quote = "\"",
      dec = ".",
      fill = TRUE,
      comment.char = "",
      stringsAsFactors = FALSE
    )
    
    out <-
      paste(
        "metadata_group",
        "zygosity",
        "colony_id",
        "depvar",
        "status",
        "code",
        "count cm",
        "count cf",
        "count mm",
        "count mf",
        sep = "\t"
      )
    
    
    vectorOutputNames <-
      c(
        "Method",
        "Dependent variable",
        "Batch included",
        "Residual variances homogeneity",
        "Genotype contribution",
        "Genotype estimate",
        "Genotype standard error",
        "Genotype p-Val",
        "Genotype percentage change",
        "Sex estimate",
        "Sex standard error",
        "Sex p-val",
        "Weight estimate",
        "Weight standard error",
        "Weight p-val",
        "Gp1 genotype",
        "Gp1 Residuals normality test",
        "Gp2 genotype",
        "Gp2 Residuals normality test",
        "Blups test",
        "Rotated residuals normality test",
        "Intercept estimate",
        "Intercept standard error",
        "Interaction included",
        "Interaction p-val",
        "Sex FvKO estimate",
        "Sex FvKO standard error",
        "Sex FvKO p-val",
        "Sex MvKO estimate",
        "Sex MvKO standard error",
        "Sex MvKO p-val",
        "Classification tag",
        "Additional information"
      )
    
    out <-
      paste(out, paste(vectorOutputNames, collapse = "\t"), sep = "\t")
    
    
    if (nrow(dataset)) {
      # make sure all controls have
      # control group clearly defined
      #
      dataset$colony_id[which(dataset$group == "control")] = "+/+"
      
      # make sure missing metadata group is marked as "NA"
      # otherwise R will need additional is.na(..) logic
      #
      dataset$metadata_group[sapply(dataset$metadata_group, is.na)] = "NA"
    }
    
    # find all metadata groups
    #
    metadata_groups <- as.character(unique(dataset$metadata_group))
    Counter  = 1
    for (metadata_group in metadata_groups) {
      print(Counter)
      local({
        #
        # select a metedata group subset
        #
        dataset_group <-
          dataset[which(dataset$metadata_group == metadata_group),]
        
        # select all controlls from the group
        #
        controls_in_group        <-
          which(dataset_group$colony_id == "+/+")
        
        num_of_controls_in_group <- length(controls_in_group)
        
        # see how many of them there are
        #
        controls_sex_combined <-
          table(dataset_group[controls_in_group,]$sex)
        
        num_control_female    <-
          as.integer(controls_sex_combined["female"])
        num_control_male      <-
          as.integer(controls_sex_combined["male"])
        
        if (is.na(num_control_female))
          num_control_female <- 0
        
        if (is.na(num_control_male))
          num_control_male <- 0
        
        
        
        # scan all colonies
        #
        colony_ids <- as.character(unique(dataset_group$colony_id))
        
        
        #colony_ids <- c("H-ITGA2-G06-TM1B", "H-CEP164-A03-TM1B", "H-ITGAE-G08-TM1B")
        #colony_ids <- c("H-CEP164-A03-TM1B")
        #colony_ids <- c("H-ITGAE-G08-TM1B")
        
        if (num_of_controls_in_group > 0) {
          #
          #
          
          controls_missing <- FALSE
        } else {
          # controls are missing
          #
          controls_missing <- TRUE
        }
        
        for (colony_id in colony_ids) {
          if (colony_id == "+/+") {
            # skip the controls
            #
            next
            
          }
          
          # ids
          #
          group_colony_id <-
            which(dataset_group$colony_id == colony_id)
          
          
          # figure out what zygosity types there are
          #
          group_colony_id_zygosity <-
            as.character(unique(dataset_group[group_colony_id,]$zygosity))
          
          for (zygname in group_colony_id_zygosity) {
            enough_data <- TRUE
            
            
            
            # select mutants with specific zygosity
            #
            group_colony_id_zygosity <- group_colony_id [sapply(group_colony_id, function(x) {
              dataset_group$zygosity[x] == zygname
            })]
            
            
            # figure out how many male and female mutants there are
            colony_zygosity_sex_combined <-
              table(sapply(group_colony_id_zygosity,
                           function(x) {
                             dataset_group$sex[x]
                           }))
            
            num_mutes_female <-
              as.integer(colony_zygosity_sex_combined["female"])
            num_mutes_male   <-
              as.integer(colony_zygosity_sex_combined["male"])
            
            if (is.na(num_mutes_female))
              num_mutes_female <- 0
            
            if (is.na(num_mutes_male))
              num_mutes_male <- 0
            
            
            
            # fail the test if there's not enough data
            #
            if (num_mutes_female < NUM_MUTANT_FEMALE_THRES &&
                num_mutes_male < NUM_MUTANT_MALE_THRES) {
              enough_data <- FALSE
              
            } else {
              enough_data <- TRUE
              
            }
            
            # combine mutants from the colony and all controlls
            #
            dataset_group_colony_zygosity_controls <- dataset_group[c(group_colony_id_zygosity, controls_in_group),]
            
            
            #num_of_controls_in_group = 1;
            
            if (enough_data && num_of_controls_in_group > 0) {
              # call PhenList only when there's enough data
              #
              dataset_phenList <- try(PhenList(
                dataset = dataset_group_colony_zygosity_controls,
                dataset.colname.genotype = "colony_id",
                testGenotype = colony_id,
                refGenotype = "+/+",
                dataset.colname.sex = "sex",
                dataset.colname.batch = "batch",
                dataset.colname.weight = "weight",
                dataset.values.male = "male",
                dataset.values.female = "female"
              ))
            }
            
            
            # select all dependent variables
            #
            names01 <- names(dataset_group_colony_zygosity_controls)
            
            depvars <-
              names01[(which(names01 == METADATA_SEPARATOR_COLUMN) + 1):length(names01)]
            
            
            # uncomment for debugging
            #depvars <- c("IMPC_ABR_012_001")
            
            for (depvar in depvars) {
              c002 <- c002 + 1
              
              
              status <- STATUS_TESTED
              
              code <- "OK"
              
              
              # check if we need to do the test
              #
              if (all(is.na(dataset_group_colony_zygosity_controls[depvar]))) {
                status <- STATUS_FAILED
                
                code   <-
                  paste("No data supplied for parameter", depvar)
                
              } else if (!enough_data) {
                status <- STATUS_FAILED
                
                code   <- "Not enough female or male measurements"
                
              } else if (num_of_controls_in_group <= 0) {
                status <- STATUS_FAILED
                
                code   <- "Missing control measurements"
                
              }
              
              if (status == STATUS_TESTED) {
                # check data type
                #
                method01 <- "MM"
                
                if (is.character(dataset_group_colony_zygosity_controls[depvar][1, ])) {
                  method01 <- "FE"
                  
                }
                
                # check if we have a specified method
                # for the variable
                #
                method02 <- getMethod(depvar, methodmap)
                
                
                if (!is.null(method02)) {
                  # we have one
                  #
                  method01 <- method02
                }
                
                
                pdf_filename = paste(
                  pdfs_dir,
                  "/",
                  basefilename,
                  "-",
                  depvar,
                  "-",
                  make.names(colony_id),
                  ".pdf",
                  sep = ""
                )
                test002 <-
                  capture.stderr(try(generate_report(
                    dataset_phenList,
					reportTitle = 'PhenStat report',
                   # reportTitle = paste(
                   #   "Stats report for colony",
                   #   colony_id,
                   #   ", parameter",
                   #   depvar
                   # ),
                    depVariable = depvar,
                    destination = pdf_filename
                  ))
                  )
                
                message(test002)
                
                
              }
              
              
              
              
              
            }
          }
        }
      }) # End local
    }
    
    
    message("test002 successfully completed")
    
    
  }




launcher <- function() {
  library("optparse")

  option_list = list(
  make_option(
  c("-f", "--file"),
  type = "character",
  default = NULL,
  help = "input file name",
  metavar = "character"
  ),
  make_option(
  c("-r", "--result"),
  type = "character",
  default = "result",
  help = "result folder name [default= %default]",
  metavar = "character"
  ),
  make_option(
  c("-m", "--mapfile"),
  type = "character",
  default = "functionMethodMap",
  help = "variable to method map [default= %default]",
  metavar = "character"
  ),
  make_option(
  c("-w", "--withWeight"),
  type = "character",
  default = FALSE,
  help = "variable to include weight in model [default= %default]",
  metavar = "logical"
  )
  )


  opt_parser = OptionParser(option_list = option_list)

  opt = parse_args(opt_parser)


  if (is.null(opt$file)) {
    print_help(opt_parser)
    stop("File name must be supplied --file=...", call. = FALSE)
  }

  test002(
  infname = opt$file,
  result = opt$result,
  methodMapfname = opt$mapfile
  )
}

launcher()
