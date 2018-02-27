#!/usr/bin/env Rscript

require(PhenStat)
require(optparse)

# DO NOT REMOVE:
#
# THIS IMPORT FIXES THE PROBELM OF 
# Rscript NOT BEING ABLE TO PROPERLY 
# LOAD S4 METHODS, WHICH RESULTS IN
# AN ERROR WHEN PhenList IS INVOKED
#
require(methods)


#
# enable JIT optimization
#
require(compiler)
setCompilerOptions(suppressAll = TRUE, optimize = 3)
enableJIT(3)


#
# function to return string w/o leading or trailing whitespace
#
trim <- function (x) gsub("^\\s+|\\s+$", "", x)


#
# function to read a support file with rows in format key=value
#
readMethodsFile <- function(fname) {

  message("Method Map:")

  flines01 <- trim(unlist(strsplit(readLines(fname), "=")));

  result01 <- list();

  for (i in seq(1, length(flines01), by = 2)) {
    result01[flines01[i]] <- flines01[i + 1]
    message(flines01[i], " = ", flines01[i + 1]);
  }

  return(result01);
}

#
# function to get the alternative stats method if exists
#
getAlternative <- function(var, methodMap = list("IMPC_ABR_" = "RR", "IMPC_ABR_0" = "FE", "IMPC_XRY_001_003" = "FE")) {

  matched001 <- sapply(names(methodMap), grepl, var)

  if (sum(matched001) > 1) {
    methodMapReduced <- methodMap[ matched001 ]
    mappedPatternLengths <- nchar(names(methodMapReduced));
    method <- unlist(methodMapReduced[ which(mappedPatternLengths ==  max(mappedPatternLengths)) ])
  } else {
    method <- unlist(methodMap[ matched001 ])
  }

  return(method);
}

#
# Define function capture.stderr which tries to execute a function and capture the output
#
capture.stderr <- function (..., file = NULL, append = FALSE) {
  args <- substitute(list(...))[-1L]
  rval <- NULL
  closeit <- TRUE
  if (is.null(file))
  file <- textConnection("rval", "w", local = TRUE)
  else if (is.character(file))
  file <- file(file, if (append) "a" else "w")
  else if (inherits(file, "connection")) {
    if (!isOpen(file))
    open(file, if (append) "a" else "w")
    else closeit <- FALSE
  }
  else stop("'file' must be NULL, a character string or a connection")
  sink(file, type="message")
  on.exit({
    sink(type="message")
    if (closeit) close(file)
  })
  pf <- parent.frame()
  evalVis <- function(expr) withVisible(eval(expr, pf))

  result01 <- c();
  for (i in seq_along(args)) {
    expr <- args[[i]]
    tmp <- switch(mode(expr), expression = lapply(expr, evalVis),
    call = , name = list(evalVis(expr)), stop("bad argument"))

    result01 <- c(result01, tmp);
    #for (item in tmp) if (item$visible)
    #    print(item$value)
  }
  on.exit()
  sink(type="message")
  if (closeit)
  close(file)
  if (is.null(rval))
  invisible(list("output"=NULL, "result"=result01))
  else list("output"=rval, "result"=result01)
}





## Test this script
#dataset <- read.delim("~/Documents/WTSI-MGP_001-IMPC_ABR-MGI5446362.tsv")
#colony_id <- "MCSR"
#metadata_group <- "ab8371fc819db4209ca1a10186a8a6ef"
#zygname <- "homozygote"
#depvar <- "IMPC_ABR_010_001"


#
# test002 function processes an input file and produces a results file of the same name with "-result" appended
#
test002 <- function(infname = "MRCHarwell-HRWL_001-IMPC_CSD-MGI2164831.tsv", result="result", methodMapfname = "functionMethodMap", equationMapfname = "functionEquationMap", withWeight=TRUE) {

  message("test002(infname = ", infname, ", result = ", result, ", methodMapfname = ", methodMapfname, ", equationMapfname = ", equationMapfname, ")", sep="");

  message("which R=", system("which R",intern=TRUE))

  methodmap <- readMethodsFile(methodMapfname);
  equationmap <- readMethodsFile(equationMapfname);


  # vars
  #
  NUM_MUTANT_FEMALE_THRES = 4
  NUM_MUTANT_MALE_THRES   = 4
  ABR_THRES = 2
  THREEI_THRES = 2

  # Is this an ABR screen
  IS_ABR = ! identical(grep("_ABR", infname, fixed=TRUE), integer(0))

  # Is this a 3I screen
  IS_THREEI = ! identical(grep("MGP_BCI|MGP_PBI|MGP_ANA|MGP_CTL|MGP_EEI|MGP_BMI|MGP_MLN|MGP_IMM|MGP_BHP|MGP_MIC", infname, fixed=FALSE), integer(0))

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
  c002 <- 0;

  # create the result folder
  #
  dir.create(result, showWarnings=FALSE, recursive=TRUE);

  #
  # create the pdfs folder if not exists
  #
  pdfs_dir = paste(dirname(result), "/pdfs", sep="")
  dir.create(pdfs_dir, showWarnings=FALSE, recursive=TRUE)
  basefilename = strsplit(basename(infname), ".", fixed=TRUE)[[1]][1]


  outfname_final = paste(result,"/", basename(infname), ".result", sep="");
  if (withWeight) {
    outfname_final = paste(result,"/", basename(infname), "-with-weight.result", sep="");
  }

  outfname = paste(outfname_final,".part", sep="");

  dataset = read.delim(infname, header = TRUE, sep = "\t", quote="\"", dec=".",
                       fill = TRUE, comment.char="", stringsAsFactors = FALSE)

  out <- paste("metadata_group", "zygosity", "colony_id", "depvar", "status", "code",
               "count cm", "count cf", "count mm", "count mf",
               "mean cm", "mean cf", "mean mm", "mean mf",
               "control_strategy", "workflow", "weight available",
               sep="\t");

  vectorOutputNames <- c("Method", "Dependent variable", "Batch included",
                         "Residual variances homogeneity", "Genotype contribution", "Genotype estimate",
                         "Genotype standard error", "Genotype p-Val", "Genotype percentage change", "Sex estimate",
                         "Sex standard error", "Sex p-val", "Weight estimate", "Weight standard error",
                         "Weight p-val", "Gp1 genotype", "Gp1 Residuals normality test", "Gp2 genotype",
                         "Gp2 Residuals normality test", "Blups test", "Rotated residuals normality test",
                         "Intercept estimate", "Intercept standard error", "Interaction included", "Interaction p-val",
                         "Sex FvKO estimate", "Sex FvKO standard error", "Sex FvKO p-val", "Sex MvKO estimate",
                         "Sex MvKO standard error", "Sex MvKO p-val", "Classification tag", "Additional information")

  out <- paste(out, paste(vectorOutputNames, collapse="\t"), sep="\t");

  write.table(out, file = outfname, append = FALSE, quote = FALSE, sep = "\t",
              eol = "\n", na = "NA", dec = ".", row.names = FALSE, col.names = FALSE)

  if (nrow(dataset)) {

    # make sure all controls have
    # control group clearly defined
    #
    dataset$colony_id[ which(dataset$group == "control") ] = "+/+"

    # make sure missing metadata group is marked as "NA"
    # otherwise R will need additional is.na(..) logic
    #
    dataset$metadata_group[ sapply(dataset$metadata_group, is.na) ] = "NA"
  }

  # find all metadata groups
  #
  metadata_groups <- as.character(unique(dataset$metadata_group))

  for (metadata_group in metadata_groups) {
    local({

      #
      # select a metedata group subset
      #
      dataset_group <- dataset[ which(dataset$metadata_group == metadata_group), ]

      # select all controlls from the group
      #
      controls_in_group        <- which(dataset_group$colony_id == "+/+");
      num_of_controls_in_group <- length(controls_in_group)

      # see how many of them there are
      #
      controls_sex_combined <- table(dataset_group[controls_in_group, ]$sex);
      num_control_female    <- as.integer(controls_sex_combined["female"])
      num_control_male      <- as.integer(controls_sex_combined["male"])

      if (is.na(num_control_female)) num_control_female <- 0;
      if (is.na(num_control_male))   num_control_male <- 0;


      # scan all colonies
      #
      colony_ids <- as.character(unique(dataset_group$colony_id));

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
          next;
        }

        # ids
        #
        group_colony_id <- which(dataset_group$colony_id == colony_id);

        # figure out what zygosity types there are
        #
        group_colony_id_zygosity <- as.character(unique(dataset_group[
          group_colony_id, ]$zygosity))

        for (zygname in group_colony_id_zygosity) {

          enough_data <- TRUE;


          # select mutants with specific zygosity
          #
          group_colony_id_zygosity <- group_colony_id [
            sapply(group_colony_id, function(x){ dataset_group$zygosity[ x ] == zygname} ) ]


          # figure out how many male and female mutants there are
          colony_zygosity_sex_combined <- table(sapply(group_colony_id_zygosity,
                                                       function(x){ dataset_group$sex[ x ] } ))

          num_mutes_female <- as.integer(colony_zygosity_sex_combined["female"])
          num_mutes_male   <- as.integer(colony_zygosity_sex_combined["male"])

          if (is.na(num_mutes_female)) num_mutes_female <- 0;
          if (is.na(num_mutes_male))   num_mutes_male <- 0;

          #
          # Test if there's not enough data for at least one sex
          #
          if (IS_ABR) {
            if (num_mutes_female >= ABR_THRES || num_mutes_male >= ABR_THRES) {
              enough_data <- TRUE;
            } else {
              # cat("setting not enough data for ABR with ", num_mutes_female, "f and ", num_mutes_male, "m for zygosity", zygname, "colony", colony_id, "\n")
              enough_data <- FALSE;
            }
          } else {
            if (num_mutes_female >= NUM_MUTANT_FEMALE_THRES || num_mutes_male >= NUM_MUTANT_MALE_THRES) {
              enough_data <- TRUE;
            } else {
              # cat("setting not enough data with ", num_mutes_female, "f and ", num_mutes_male, "m for zygosity", zygname, "colony", colony_id, "\n")
              enough_data <- FALSE;
            }
          }

          #
          # combine mutants from the colony and all appropriate controlls
          #
          dataset_group_colony_zygosity_controls <- dataset_group[ c(group_colony_id_zygosity, controls_in_group), ]

          if (enough_data && num_of_controls_in_group > 0) {


            # call PhenList only when there's enough data
            #
            dataset_phenList <- try(PhenList(
              dataset=dataset_group_colony_zygosity_controls, dataset.colname.genotype="colony_id",
              testGenotype=colony_id, refGenotype="+/+",
              dataset.colname.sex="sex", dataset.colname.batch="batch", dataset.colname.weight="weight",
              dataset.values.male="male", dataset.values.female="female"))
          }


          # select all dependent variables
          #
          names01 <- names(dataset_group_colony_zygosity_controls);
          depvars <- names01[ (which(names01 == METADATA_SEPARATOR_COLUMN) + 1):length(names01) ]


          # uncomment for debugging
          #depvars <- c("IMPC_ABR_012_001")

          for(depvar in depvars) {

            mappedColumn <- paste(depvar, "_MAPPED", sep = "")
            if (mappedColumn %in% depvars) {
              next
            }

            # Failed to create a dataframe, skip
            if ( ! "dataset_phenList" %in% ls() || class(dataset_phenList)=="try-error") {
              out <- paste(metadata_group, zygname, colony_id, depvar, "FAILED", "Not enough mutant or control data",
                           num_control_male, num_control_female, num_mutes_male, num_mutes_female,
                           "-", "-", "-", "-",
                           "-", "-", "-",
                           sep="\t");

              write.table(out, file = outfname, append = TRUE, quote = FALSE, sep = "\t",
                            eol = "\n", na = "NA", dec = ".", row.names = FALSE, col.names = FALSE);
              next
            }

            operational_dataset <- dataset_phenList

            # Slice the dataframe down to just the required columns
            include_columns = c("Batch", "Genotype", "group", "Sex", "zygosity", depvar)
            if ("Weight" %in% colnames(operational_dataset@datasetPL)) {
              include_columns = c(include_columns, "Weight")
            }
            operational_dataset@datasetPL = operational_dataset@datasetPL[,include_columns]


            # Calculate the number of specimens for this dataset
            count_female_controls_in_group <- length(which(!is.na(operational_dataset@datasetPL[which(operational_dataset@datasetPL$Sex == "Female" & operational_dataset@datasetPL$Genotype=="+/+"),][depvar])))
            count_male_controls_in_group   <- length(which(!is.na(operational_dataset@datasetPL[which(operational_dataset@datasetPL$Sex == "Male" & operational_dataset@datasetPL$Genotype=="+/+"),][depvar])))
            count_female_mutants_in_group  <- length(which(!is.na(operational_dataset@datasetPL[which(operational_dataset@datasetPL$Sex == "Female" & operational_dataset@datasetPL$Genotype==colony_id),][depvar])))
            count_male_mutants_in_group    <- length(which(!is.na(operational_dataset@datasetPL[which(operational_dataset@datasetPL$Sex == "Male" & operational_dataset@datasetPL$Genotype==colony_id),][depvar])))

            num_control_female <- count_female_controls_in_group
            num_control_male   <- count_male_controls_in_group
            num_mutes_female   <- count_female_mutants_in_group
            num_mutes_male     <- count_male_mutants_in_group

            if (is.na(num_control_female)) num_control_female <- 0;
            if (is.na(num_control_male))   num_control_male <- 0;
            if (is.na(num_mutes_female))   num_mutes_female <- 0;
            if (is.na(num_mutes_male))     num_mutes_male <- 0;

            #
            # Test if there's not enough data for at least one sex per depvar
            #
            if (IS_ABR) {
              if (num_mutes_female >= ABR_THRES || num_mutes_male >= ABR_THRES) {
                enough_data <- TRUE;
              } else {
                # cat("setting not enough data for ABR with ", num_mutes_female, "f and ", num_mutes_male, "m for zygosity", zygname, "colony", colony_id)
                enough_data <- FALSE;
              }
            } else {
              if (num_mutes_female >= NUM_MUTANT_FEMALE_THRES || num_mutes_male >= NUM_MUTANT_MALE_THRES) {
                enough_data <- TRUE;
              } else {
                # cat("setting not enough data with ", num_cmutes_female, "f and ", num_mutes_male, "m for zygosity", zygname, "colony", colony_id)
                enough_data <- FALSE;
              }
            }

            # Skip parameters than have no data at all
            if ( num_mutes_female + num_mutes_male < 1) {
              message(c002, " -----------> ", metadata_group, ":", colony_id, ":", zygname, ":", depvar, ":Not processing - not enough mutant data");
              next
            }

            # Default workflow is multi_batch
            workflow <- "multi_batch"

            # Get the batch column for only this depvar
            batches <- operational_dataset@datasetPL[which(!is.na(operational_dataset@datasetPL[depvar])), ]$batch
            colony_ids_for_strat <- operational_dataset@datasetPL[which(!is.na(operational_dataset@datasetPL[depvar])), ]$colony_id
            sexes_for_strat <- operational_dataset@datasetPL[which(!is.na(operational_dataset@datasetPL[depvar])), ]$sex


            starter_female <- operational_dataset@datasetPL[which(sexes_for_strat=="female" & colony_ids_for_strat==colony_id), ]
            starter_male <- operational_dataset@datasetPL[which(sexes_for_strat=="male" & colony_ids_for_strat==colony_id), ]
            female_mutant_batches <- unique(starter_female[!is.na(starter_female[depvar]), ]$batch)
            male_mutant_batches <- unique(starter_male[!is.na(starter_male[depvar]), ]$batch)
            combined_batches <- unique(c(female_mutant_batches, male_mutant_batches))

            if ( (length(male_mutant_batches)==0 && length(female_mutant_batches)>0) ||
                 (length(female_mutant_batches)==0 && length(male_mutant_batches)>0) ) {
              workflow <- "one_sex_only"

            } else if ( length(combined_batches) == 1 ) {
              workflow <- "one_batch"

            } else if ( length(combined_batches) <= 3 ) {
              workflow <- "low_batch"

            } else if ( length(male_mutant_batches) >=3 && length(female_mutant_batches) >= 2 ||
                        length(female_mutant_batches) && length(male_mutant_batches) >= 2 ) {
              workflow <- "multi_batch"
            }

            # Default strategy is all appropriate controls
            control_strategy <- "baseline_all"

            # Test if this dataset can be analyzed using concurrent control strategy and
            # update the dataframe to contain only the concurrent data if so
            if (length(unique(batches[colony_ids_for_strat==colony_id]))==1) {

              unique_batch_date = unique(batches[colony_ids_for_strat==colony_id])[1]
              if (length(batches[colony_ids_for_strat!=colony_id & sexes_for_strat=="male" & batches==unique_batch_date]) >= 6 &&
                  length(batches[colony_ids_for_strat!=colony_id & sexes_for_strat=="female" & batches==unique_batch_date]) >=6) {
                # Enough control points found for Concurrent strategy
                control_strategy <- "concurrent"

                # update the dataframe to include only concurrent values for this dataset
                # dataset is dep-var and colony_id

                operational_dataset@datasetPL = operational_dataset@datasetPL[operational_dataset@datasetPL$Batch==unique_batch_date, ]
                workflow <- "one_batch"

              }
            }




            # check data type
            #
            method01 <- "MM";
            if (is.character(dataset_group_colony_zygosity_controls[depvar][1,])) {
              method01 <- "FE";
              mean_control_female    <- "-"
              mean_control_male      <- "-"
              mean_mutes_female     <- "-"
              mean_mutes_male       <- "-"
            }  else {

              male_controls_in_group <- operational_dataset@datasetPL[which(operational_dataset@datasetPL$Sex == "Male" & operational_dataset@datasetPL$Genotype=="+/+"),][depvar]
              female_controls_in_group <- operational_dataset@datasetPL[which(operational_dataset@datasetPL$Sex == "Female" & operational_dataset@datasetPL$Genotype=="+/+"),][depvar]
              male_mutants_in_group <- operational_dataset@datasetPL[which(operational_dataset@datasetPL$Sex == "Male" & operational_dataset@datasetPL$Genotype==colony_id),][depvar]
              female_mutants_in_group <- operational_dataset@datasetPL[which(operational_dataset@datasetPL$Sex == "Female" & operational_dataset@datasetPL$Genotype==colony_id),][depvar]

              if ( ! all(is.na(female_controls_in_group))) {
                mean_control_female    <- sum(female_controls_in_group, na.rm = TRUE) / length(female_controls_in_group[!is.na(female_controls_in_group)])
              } else {
                mean_control_female = "-"
              }

              if ( ! all(is.na(male_controls_in_group))) {
                mean_control_male      <- sum(male_controls_in_group, na.rm = TRUE) / length(male_controls_in_group[!is.na(male_controls_in_group)])
              } else {
                mean_control_male = "-"
              }

              if ( ! all(is.na(female_mutants_in_group))) {
                mean_mutes_female <- sum(female_mutants_in_group, na.rm = TRUE) / length(female_mutants_in_group[!is.na(female_mutants_in_group)])
              } else {
                mean_mutes_female = "-"
              }

              if ( ! all(is.na(male_mutants_in_group))) {
                mean_mutes_male   <- sum(male_mutants_in_group, na.rm = TRUE) / length(male_mutants_in_group[!is.na(male_mutants_in_group)])
              } else {
                mean_mutes_male = "-"
              }

            }



            # check if we have a specified method
            # for the variable
            #
            method02 <- getAlternative(depvar, methodmap);

            if (!is.null(method02)) {

              # we have one
              #
              # cat("Using method", method02, "for variable", depvar)
              method01 <- method02
            }

            #
            # Override the PhenStat equation used (with or without modelling in weight)
            #
            eq <- EQUATION
            alt_equation <- getAlternative(depvar, equationmap)
            if ( ! is.null(alt_equation)) {
              eq <- alt_equation
            }
            EQUATION <- eq

            c002 <- c002 + 1;

            status <- STATUS_TESTED;
            code <- "OK";

            # check if we need to do the test
            #
            if (all(is.na(operational_dataset@datasetPL[depvar]))) {

              status <- STATUS_FAILED;
              code   <- paste("No data supplied for parameter", depvar);
            } else if (!enough_data) {

              status <- STATUS_FAILED;
              code   <- paste("Not enough mutant measurements (", num_mutes_female, "f/", num_mutes_male,"m)", sep="");
            } else if (num_of_controls_in_group <= 0) {

              status <- STATUS_FAILED;
              code   <- "Missing control measurements";
            }

            weight_available <- FALSE

            if (status == STATUS_TESTED) {

              # If there are not enough of one sex to allow for processing, filter that sex out and continue with the other
              if (num_mutes_female < NUM_MUTANT_FEMALE_THRES && (IS_ABR && num_mutes_female < ABR_THRES) ) {

                # Too few females, create a new PhenList with just te males

                operational_dataset <- dataset_phenList
                operational_dataset@datasetPL = operational_dataset@datasetPL[,include_columns]
                operational_dataset <- try(PhenList(
                  dataset=operational_dataset@datasetPL[operational_dataset@datasetPL$Sex=="Male", ], dataset.colname.genotype="Genotype",
                  testGenotype=colony_id, refGenotype="+/+",
                  dataset.colname.sex="Sex", dataset.colname.batch="Batch", dataset.colname.weight="weight",
                  dataset.values.male="male", dataset.values.female="female"))
              } else if (num_mutes_male < NUM_MUTANT_MALE_THRES && (IS_ABR && num_mutes_male < ABR_THRES) ) {

                # Too few males, create a new PhenList with just the females

                operational_dataset <- dataset_phenList
                operational_dataset@datasetPL = operational_dataset@datasetPL[,include_columns]
                operational_dataset <- try(PhenList(
                  dataset=operational_dataset@datasetPL[operational_dataset@datasetPL$Sex=="Female", ], dataset.colname.genotype="Genotype",
                  testGenotype=colony_id, refGenotype="+/+",
                  dataset.colname.sex="Sex", dataset.colname.batch="Batch", dataset.colname.weight="weight",
                  dataset.values.male="male", dataset.values.female="female"))
              }

              # do the test
              #
              # capture.stderr(...) captures the object and the stderr
              # try(...) doens't allow R to exit the routine prematurely
              #

              # Only test the data set if there is any variability in the data
              if ( nlevels(factor(operational_dataset@datasetPL[[depvar]])) > 1) {
                test001 <- capture.stderr(try(testDataset(operational_dataset, depVariable=depvar, method=method01, equation=EQUATION), silent = TRUE));

                if ( IS_ABR && ! identical(grep("to allow the application of RR plus framework", test001$output, fixed=TRUE), integer(0)) && grep("to allow the application of RR plus framework", test001$output, fixed=TRUE) > 0 ) {
                  # Cannot perform RR+ because not enough control points, try MM

                  test001 <- capture.stderr(try(testDataset(operational_dataset, depVariable=depvar, method="MM", equation=EQUATION, dataPointsThreshold = ABR_THRES), silent = TRUE));

                  if ( IS_ABR && ! identical(grep("jitter", test001$output, fixed=TRUE), integer(0)) && grep("jitter", test001$output, fixed=TRUE) > 0 ) {

                    # Per Terry 2017-08-08
                    # ABR data sets will not fit the MM if the specimen is completely deaf -- all values will be equal
                    # In these cases (identified by PhenStat with a suggestion to try jitter), add jitter to the values and try again

                    operational_dataset@datasetPL[[depvar]] = jitter(operational_dataset@datasetPL[[depvar]], 0.1)
                    test001 <- capture.stderr(try(testDataset(operational_dataset, depVariable=depvar, method="MM", equation=EQUATION, dataPointsThreshold = ABR_THRES), silent = TRUE));

                  }

                }
              } else {
                test001 <- capture.stderr(try(testDataset(operational_dataset, depVariable=depvar, method="MM", equation=EQUATION, dataPointsThreshold = ABR_THRES), silent = TRUE));
                status <- "Not tested (No variation)"
                output <- rep(MISSING_DATA_VALUE, length(vectorOutputNames));
                names(output) <- vectorOutputNames;
                if( !enough_data) {
                  code   <- paste("Not enough mutant measurements (", num_mutes_female, "f/", num_mutes_male,"m)", sep="");
                }
              }


              if ( c('Weight') %in% colnames(operational_dataset@datasetPL) ) {
                weight_available <- TRUE
              }


              # test the test result is not error
              if ("test001" %in% ls() && class(test001$result[[1]]$value) == "try-error") {

                status <- STATUS_FAILED;

                index001 <- grep("Error:", test001$output)

                if (length(index001) > 0) {
                  # obtain the error message
                  #
                  code   <- paste(test001$output[index001:index001+1], collapse=" ")
                } else {
                  # default message
                  #
                  code   <- "testDataset returned an error, check log file"
                }

              } else {
                # everything seems OK
                #
                output <- PhenStat:::vectorOutput(test001$result[[1]]$value, phenotypeThreshold = 0.0001);
              }

              #pdf_filename = paste(pdfs_dir, "/", basefilename, "-", depvar, "-", make.names(colony_id), ".pdf", sep="")
              #test002 <- capture.stderr(try(generate_report(dataset_phenList, reportTitle=paste("PhenStat report for colony",colony_id,", parameter",depvar), depVariable=depvar, destination=pdf_filename) ));
              #message(test002);

            }


            if (status == STATUS_FAILED) {

              # model the output vector
              # with X in all values
              #
              output <- rep(MISSING_DATA_VALUE, length(vectorOutputNames));
              names(output) <- vectorOutputNames;
            }

            message(c002, " -----------> ", metadata_group, ":", colony_id, ":",
                    zygname, ":", depvar, ":", status, ":", code);

            out <- paste(metadata_group, zygname, colony_id, depvar, status, code,
                         num_control_male, num_control_female, num_mutes_male, num_mutes_female,
                         mean_control_male, mean_control_female, mean_mutes_male, mean_mutes_female,
                         control_strategy, workflow, weight_available,
                         sep="\t");

            # providing the data order doesn't change in PhenStat
            # we can do simple paste(output, collapse="\t")
            #
            # but taking values by their names will proove to be
            # more stable/robust in the long run if the order or names change
            #
            out <- paste(out, paste(output[ vectorOutputNames ], collapse="\t"), sep="\t");

            if (STATUS_FAILED == "FAILED" && !((num_control_male + num_control_female + num_mutes_male + num_mutes_female) == 0) ) {
              write.table(out, file = outfname, append = TRUE, quote = FALSE, sep = "\t",
                          eol = "\n", na = "NA", dec = ".", row.names = FALSE, col.names = FALSE);
            }

          }
        }
      }
    }) # End local
  }

  # rename the result file from temporary to final name
  #
  file.rename(outfname, outfname_final);

  message("test002 successfully completed");

}


#
# Runs the test002 function on an input file
#
launcher <- function(){

  library("optparse")

  option_list = list(
  make_option(c("-f", "--file"), type="character", default=NULL,
  help="input file name", metavar="character"),
  make_option(c("-r", "--result"), type="character", default="result",
  help="result folder name [default= %default]", metavar="character"),
  make_option(c("-m", "--mapfile"), type="character", default="functionMethodMap",
  help="variable to method map [default= %default]", metavar="character"),
  make_option(c("-e", "--eqfile"), type="character", default="functionEquationMap",
  help="variable to equation map [default= %default]", metavar="character"),
  make_option(c("-w", "--withWeight"), type="character", default=FALSE,
  help="variable to include weight in model [default= %default]", metavar="logical")
  );

  opt_parser = OptionParser(option_list=option_list);
  opt = parse_args(opt_parser);

  if (is.null(opt$file)){
    print_help(opt_parser)
    stop("File name must be supplied --file=...", call.=FALSE)
  }

  test002(infname=opt$file, result=opt$result, methodMapfname=opt$mapfile, equationMapfname=opt$eqfile)
}


launcher()



