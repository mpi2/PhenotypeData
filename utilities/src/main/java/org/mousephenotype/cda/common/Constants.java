package org.mousephenotype.cda.common;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Contants used in the IMPC data portal project
 */
public class Constants {


    // Data type contants
    public static final String UNIDIMENSIONAL_DATATYPE = "unidimensional";
    public static final String TIME_SERIES_DATATYPE = "time_series";
    public static final String TEXT_DATATYPE = "text";
    public static final String CATEGORICAL_DATATYPE = "categorical";
    public static final String IMAGE_RECORD_DATATYPE = "image_record";

    // IMPC default p-value threshold used to indicate significance
    public static final double P_VALUE_THRESHOLD = 0.0001;
    public static final String SIGNIFICANT_P_VALUE_HTML = "1.00x10<sup>-4</sup>";
    public static final String SIGNIFICANT_P_VALUE_TEXT = "1.00x10-4";
    public static final String MINUS_LOG10_HTML = "-Log<sub>10</sub>";

    // Brackets denote optional components Single quotes denote literal characters.
    public static final String DATETIME_FORMAT_OPTIONAL_MILLISECONDS = "yyyy-MM-dd['T'][' ']HH:mm:ss[.SSS]['Z']";

    // Only include images in the resource that have the following paths
    public static final Set<String> INCLUDE_IMAGE_PATHS = new HashSet<>(Arrays.asList("mousephenotype.org", "file:///nfs/komp2/web/images/3i"));

    // Parameters to plot for ABR, in this order
    public static final List<String> ABR_PARAMETERS = Arrays.asList(
            "IMPC_ABR_002_001", "IMPC_ABR_004_001", "IMPC_ABR_006_001",
            "IMPC_ABR_008_001", "IMPC_ABR_010_001", "IMPC_ABR_012_001");

    public static final List<String> viabilityParameters = Arrays.asList(
            "IMPC_VIA_001_001", "IMPC_VIA_002_001", "IMPC_EVL_001_001",
            "IMPC_EVM_001_001", "IMPC_EVP_001_001", "IMPC_EVO_001_001",
            "IMPC_VIA_063_001", "IMPC_VIA_064_001", "IMPC_VIA_065_001",
            "IMPC_VIA_066_001", "IMPC_VIA_067_001");

    // 03-Aug-2017 (mrelac) Do not include IMPC_VIA_002_001 in this list unless you want duplicate genes that qualify for both
    // IMPC_VIA_001_001 and IMPC_VIA_002_001 (see gene_symbol App or Ctsd, for example)
    public static final List<String> adultViabilityParameters = Arrays.asList(
            "IMPC_VIA_001_001", "IMPC_VIA_063_001", "IMPC_VIA_064_001",
            "IMPC_VIA_065_001", "IMPC_VIA_066_001", "IMPC_VIA_067_001");


    public static final List<String> weightParameters = Arrays.asList(
            "'IMPC_GRS_003_001'", "'IMPC_CAL_001_001'", "'IMPC_DXA_001_001'",
            "'IMPC_HWT_007_001'", "'IMPC_PAT_049_001'", "'IMPC_BWT_001_001'",
            "'IMPC_ABR_001_001'", "'IMPC_CHL_001_001'", "'TCP_CHL_001_001'",
            "'HMGU_ROT_004_001'", "'ESLIM_001_001_001'", "'ESLIM_002_001_001'",
            "'ESLIM_003_001_001'", "'ESLIM_004_001_001'", "'ESLIM_005_001_001'",
            "'ESLIM_020_001_001'", "'ESLIM_022_001_001'", "'ESLIM_009_001_003'",
            "'ESLIM_010_001_003'", "'ESLIM_011_001_011'", "'ESLIM_012_001_005'",
            "'ESLIM_013_001_018'", "'ESLIM_022_001_001'", "'GMC_916_001_022'",
            "'GMC_908_001_001'", "'GMC_900_001_001'", "'GMC_926_001_003'",
            "'GMC_922_001_002'", "'GMC_923_001_001'", "'GMC_921_001_002'",
            "'GMC_902_001_003'", "'GMC_912_001_018'", "'GMC_917_001_001'",
            "'GMC_920_001_001'", "'GMC_909_001_002'", "'GMC_914_001_001'");


    public static final List<String> ESLIM_702 = Arrays.asList(
            "ESLIM_022_001_709", "ESLIM_022_001_710","ESLIM_022_001_711",
            "ESLIM_022_001_712","ESLIM_022_001_713", "ESLIM_022_001_001");
    //Arrays.asList("ESLIM_009_001_003", "ESLIM_010_001_003", "ESLIM_011_001_011", "ESLIM_012_001_005", "ESLIM_013_001_018", "ESLIM_022_001_001");

    public static final List<String> ESLIM_701 = Arrays.asList(
            "ESLIM_022_001_703", "ESLIM_022_001_704","ESLIM_022_001_705",
            "ESLIM_022_001_706","ESLIM_022_001_707","ESLIM_022_001_708");
    // Arrays.asList("ESLIM_001_001_001", "ESLIM_002_001_001", "ESLIM_003_001_001", "ESLIM_004_001_001", "ESLIM_005_001_001", "ESLIM_020_001_001", "ESLIM_022_001_001");

    public static final List<String> IMPC_BWT = Arrays.asList(
            "IMPC_GRS_003_001", "IMPC_CAL_001_001", "IMPC_DXA_001_001",
            "IMPC_HWT_007_001", "IMPC_PAT_049_001", "IMPC_BWT_001_001",
            "IMPC_ABR_001_001", "IMPC_CHL_001_001", "TCP_CHL_001_001",
            "HMGU_ROT_004_001");

    public static final List<String> IMPC_IPG_002_001 = Arrays.asList(
            "IMPC_IPG_012_001", "IMPC_IPG_011_001", "IMPC_IPG_010_001");

    public static Set<String> DERIVED_BODY_WEIGHT_PARAMETERS=new HashSet<>(Arrays.asList(
            "IMPC_BWT_008_001", "ESLIM_022_001_701", "ESLIM_022_001_702"));

    // Displayable messages
    public static final String[] EMPTY_ROW                = new String[]{""};
    public static final String   NONE                     = "None";
    public static final String   NO_DATA                  = "No data";
    public static final String   NO_INFORMATION_AVAILABLE = "No information available";
    public static final String   IMAGE_COMING_SOON        = "Image coming soon<br>";

    // PDF thumbnail icon (so PDFs show a PDF icon.
    public static final String PDF_THUMBNAIL_RELATIVE_URL = "img/filetype_pdf.png";



}