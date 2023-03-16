#!/usr/bin/python
import platform
import argparse
import locale
import time
import omero
import sys
import collections
import logging
import omero.all
import omero.rtypes
import omero
import omero.cli
from omero.gateway import *
from omero.rtypes import wrap
from omero.model import DatasetI, ProjectI
from common import splitString
from OmeroPropertiesParser import OmeroPropertiesParser
import psycopg2

DS_DICT = {'BCM-BCMLA_001-BCMLA_EYE_001-BCMLA_EYE_074_001': 611,
           'BCM-BCMLA_001-BCMLA_EYE_001-BCMLA_EYE_075_001': 612,
           'BCM-BCMLA_001-BCMLA_EYE_001-BCMLA_EYE_078_001': 619,
           'BCM-BCMLA_001-BCMLA_EYE_001-BCMLA_EYE_079_001': 657,
           'BCM-BCMLA_001-BCMLA_EYE_003-BCMLA_EYE_074_001': 803,
           'BCM-BCMLA_001-BCMLA_EYE_003-BCMLA_EYE_075_001': 801,
           'BCM-BCMLA_001-BCMLA_EYE_003-BCMLA_EYE_078_001': 810,
           'BCM-BCMLA_001-BCMLA_EYE_003-BCMLA_EYE_079_001': 812,
           'BCM-BCMLA_001-BCMLA_XRY_001-BCMLA_XRY_034_001': 653,
           'BCM-BCMLA_001-BCMLA_XRY_001-BCMLA_XRY_048_001': 655,
           'BCM-BCMLA_001-BCMLA_XRY_001-BCMLA_XRY_049_001': 656,
           'BCM-BCMLA_001-BCMLA_XRY_001-BCMLA_XRY_050_001': 651,
           'BCM-BCMLA_001-BCMLA_XRY_001-BCMLA_XRY_051_001': 652,
           'BCM-BCM_001-IMPC_ALZ_001-IMPC_ALZ_076_001': 101,
           'BCM-BCM_001-IMPC_ELZ_001-IMPC_ELZ_064_001': 1,
           'BCM-BCM_001-IMPC_EMA_001-IMPC_EMA_001_001': 102,
           'BCM-BCM_001-IMPC_EYE_001-IMPC_EYE_074_001': 452,
           'BCM-BCM_001-IMPC_EYE_001-IMPC_EYE_075_001': 457,
           'BCM-BCM_001-IMPC_EYE_001-IMPC_EYE_078_001': 455,
           'BCM-BCM_001-IMPC_EYE_001-IMPC_EYE_079_001': 453,
           'BCM-BCM_001-IMPC_HIS_002-IMPC_HIS_177_001': 621,
           'BCM-BCM_001-IMPC_HIS_003-IMPC_HIS_177_001': 953,
           'BCM-BCM_001-IMPC_XRY_001-IMPC_XRY_034_001': 103,
           'BCM-BCM_001-IMPC_XRY_001-IMPC_XRY_048_001': 104,
           'BCM-BCM_001-IMPC_XRY_001-IMPC_XRY_049_001': 105,
           'BCM-BCM_001-IMPC_XRY_001-IMPC_XRY_050_001': 106,
           'BCM-BCM_001-IMPC_XRY_001-IMPC_XRY_051_001': 107,
           'BCM-IMPC_001-IMPC_EYE_001-IMPC_EYE_073_001': 351,
           'BCM-IMPC_001-IMPC_EYE_001-IMPC_EYE_074_001': 361,
           'BCM-IMPC_001-IMPC_EYE_001-IMPC_EYE_075_001': 364,
           'BCM-IMPC_001-IMPC_EYE_001-IMPC_EYE_078_001': 356,
           'BCM-IMPC_001-IMPC_EYE_001-IMPC_EYE_079_001': 406,
           'BCM-IMPC_001-IMPC_XRY_001-IMPC_XRY_034_001': 251,
           'BCM-IMPC_001-IMPC_XRY_001-IMPC_XRY_048_001': 252,
           'BCM-IMPC_001-IMPC_XRY_001-IMPC_XRY_049_001': 253,
           'BCM-IMPC_001-IMPC_XRY_001-IMPC_XRY_050_001': 254,
           'BCM-IMPC_001-IMPC_XRY_001-IMPC_XRY_051_001': 255,
           'CCP-IMG-ALTCCP_001-ALTCCP_XRY_001-ALTCCP_XRY_034_001': 807,
           'CCP-IMG-ALTCCP_001-ALTCCP_XRY_001-ALTCCP_XRY_048_001': 809,
           'CCP-IMG-ALTCCP_001-ALTCCP_XRY_001-ALTCCP_XRY_049_001': 853,
           'CCP-IMG-ALTCCP_001-ALTCCP_XRY_001-ALTCCP_XRY_050_001': 817,
           'CCP-IMG-ALTCCP_001-ALTCCP_XRY_001-ALTCCP_XRY_051_001': 816,
           'CCP-IMG-ALTCCP_001-ALTCCP_XRY_001-ALTCCP_XRY_052_001': 852,
           'CCP-IMG-CCP_001-CCP_XRY_001-CCP_XRY_034_001': 609,
           'CCP-IMG-CCP_001-CCP_XRY_001-CCP_XRY_048_001': 606,
           'CCP-IMG-CCP_001-CCP_XRY_001-CCP_XRY_049_001': 582,
           'CCP-IMG-CCP_001-CCP_XRY_001-CCP_XRY_050_001': 604,
           'CCP-IMG-CCP_001-CCP_XRY_001-CCP_XRY_051_001': 581,
           'CCP-IMG-CCP_001-CCP_XRY_001-CCP_XRY_052_001': 603,
           'CCP-IMG-CCP_001-IMPC_ALZ_001-IMPC_ALZ_075_001': 607,
           'CCP-IMG-CCP_001-IMPC_ELZ_001-IMPC_ELZ_064_001': 580,
           'HMGU-HMGULA_001-HMGULA_XRY_001-HMGULA_XRY_034_001': 584,
           'HMGU-HMGULA_001-HMGULA_XRY_001-HMGULA_XRY_048_001': 579,
           'HMGU-HMGU_001-IMPC_ALZ_001-IMPC_ALZ_076_001': 2,
           'HMGU-HMGU_001-IMPC_ECG_001-IMPC_ECG_025_001': 458,
           'HMGU-HMGU_001-IMPC_ECG_003-IMPC_ECG_025_001': 901,
           'HMGU-HMGU_001-IMPC_XRY_001-IMPC_XRY_034_001': 3,
           'HMGU-HMGU_001-IMPC_XRY_001-IMPC_XRY_048_001': 4,
           'ICS-ICSLA_001-ICSLA_XRY_001-ICSLA_XRY_034_001': 554,
           'ICS-ICSLA_001-ICSLA_XRY_001-ICSLA_XRY_048_001': 575,
           'ICS-ICS_001-IMPC_ECH_001-IMPC_ECH_025_001': 5,
           'ICS-ICS_001-IMPC_ELZ_001-IMPC_ELZ_063_001': 108,
           'ICS-ICS_001-IMPC_ELZ_001-IMPC_ELZ_064_001': 109,
           'ICS-ICS_001-IMPC_GEL_003-IMPC_GEL_044_001': 201,
           'ICS-ICS_001-IMPC_PAT_001-IMPC_PAT_057_001': 6,
           'ICS-ICS_001-IMPC_PAT_002-IMPC_PAT_057_002': 7,
           'ICS-ICS_001-IMPC_XRY_001-IMPC_XRY_034_001': 8,
           'ICS-ICS_001-IMPC_XRY_001-IMPC_XRY_048_001': 9,
           'JAX-JAXLA_001-JAXLA_CSD_001-JAXLA_CSD_085_001': 578,
           'JAX-JAXLA_001-JAXLA_CSD_003-JAXLA_CSD_085_001': 751,
           'JAX-JAXLA_001-JAXLA_ECG_002-JAXLA_ECG_025_001': 620,
           'JAX-JAXLA_001-JAXLA_ECG_003-JAXLA_ECG_025_001': 806,
           'JAX-JAXLA_001-JAXLA_ERG_003-JAXLA_ERG_028_001': 952,
           'JAX-JAXLA_001-JAXLA_EYE_001-JAXLA_EYE_050_001': 565,
           'JAX-JAXLA_001-JAXLA_XRY_001-JAXLA_XRY_048_001': 954,
           'JAX-JAXLA_001-JAXLA_XRY_001-JAXLA_XRY_051_001': 951,
           'JAX-JAX_001-IMPC_ABR_002-IMPC_ABR_014_001': 617,
           'JAX-JAX_001-IMPC_ABR_002-IMPC_ABR_019_001': 702,
           'JAX-JAX_001-IMPC_ALZ_001-IMPC_ALZ_075_001': 10,
           'JAX-JAX_001-IMPC_CSD_001-IMPC_CSD_085_001': 11,
           'JAX-JAX_001-IMPC_CSD_003-IMPC_CSD_085_001': 752,
           'JAX-JAX_001-IMPC_ECG_002-IMPC_ECG_025_001': 601,
           'JAX-JAX_001-IMPC_ECG_003-IMPC_ECG_025_001': 818,
           'JAX-JAX_001-IMPC_ELZ_001-IMPC_ELZ_064_001': 12,
           'JAX-JAX_001-IMPC_EYE_001-IMPC_EYE_050_001': 460,
           'JAX-JAX_001-IMPC_EYE_001-IMPC_EYE_051_001': 456,
           'JAX-JAX_001-IMPC_GEL_003-IMPC_GEL_044_001': 13,
           'JAX-JAX_001-IMPC_GEM_003-IMPC_GEM_049_001': 14,
           'JAX-JAX_001-IMPC_GEO_003-IMPC_GEO_050_001': 15,
           'JAX-JAX_001-IMPC_GEP_003-IMPC_GEP_064_001': 16,
           'JAX-JAX_001-IMPC_GPL_001-IMPC_GPL_007_001': 614,
           'JAX-JAX_001-IMPC_GPO_001-IMPC_GPO_007_001': 618,
           'JAX-JAX_001-IMPC_GPP_001-IMPC_GPP_007_001': 459,
           'JAX-JAX_001-IMPC_XRY_001-IMPC_XRY_034_001': 17,
           'JAX-JAX_001-IMPC_XRY_001-IMPC_XRY_048_001': 18,
           'JAX-JAX_001-IMPC_XRY_001-IMPC_XRY_049_001': 19,
           'JAX-JAX_001-IMPC_XRY_001-IMPC_XRY_050_001': 20,
           'JAX-JAX_001-IMPC_XRY_001-IMPC_XRY_051_001': 21,
           'JAX-JAX_001-JAX_ERG_001-JAX_ERG_027_001': 22,
           'JAX-JAX_001-JAX_ERG_001-JAX_ERG_028_001': 23,
           'JAX-JAX_001-JAX_ERG_002-JAX_ERG_047_001': 24,
           'JAX-JAX_001-JAX_SLW_001-JAX_SLW_016_001': 25,
           'KMPC-IMPC_001-IMPC_ELZ_001-IMPC_ELZ_064_001': 363,
           'KMPC-IMPC_001-IMPC_EYE_001-IMPC_EYE_050_001': 362,
           'KMPC-IMPC_001-IMPC_EYE_001-IMPC_EYE_051_001': 352,
           'KMPC-IMPC_001-IMPC_EYE_002-IMPC_EYE_050_001': 815,
           'KMPC-IMPC_001-IMPC_EYE_002-IMPC_EYE_051_001': 804,
           'KMPC-IMPC_001-IMPC_GEL_003-IMPC_GEL_044_001': 403,
           'KMPC-IMPC_001-IMPC_XRY_001-IMPC_XRY_034_001': 354,
           'KMPC-IMPC_001-IMPC_XRY_001-IMPC_XRY_048_001': 360,
           'KMPC-IMPC_001-IMPC_XRY_001-IMPC_XRY_049_001': 402,
           'KMPC-IMPC_001-IMPC_XRY_001-IMPC_XRY_050_001': 404,
           'KMPC-IMPC_001-IMPC_XRY_001-IMPC_XRY_051_001': 355,
           'KMPC-IMPC_001-IMPC_XRY_001-IMPC_XRY_052_001': 359,
           'KMPC-KMPCLA_001-KMPCLA_EYE_001-KMPCLA_EYE_051_001': 572,
           'KMPC-KMPCLA_001-KMPCLA_EYE_003-KMPCLA_EYE_050_001': 813,
           'KMPC-KMPCLA_001-KMPCLA_EYE_003-KMPCLA_EYE_051_001': 814,
           'KMPC-KMPCLA_001-KMPCLA_XRY_001-KMPCLA_XRY_034_001': 576,
           'KMPC-KMPCLA_001-KMPCLA_XRY_001-KMPCLA_XRY_048_001': 574,
           'KMPC-KMPCLA_001-KMPCLA_XRY_001-KMPCLA_XRY_049_001': 562,
           'KMPC-KMPCLA_001-KMPCLA_XRY_001-KMPCLA_XRY_050_001': 573,
           'KMPC-KMPCLA_001-KMPCLA_XRY_001-KMPCLA_XRY_051_001': 555,
           'KMPC-KMPCLA_001-KMPCLA_XRY_001-KMPCLA_XRY_052_001': 568,
           'MARC-IMPC_001-IMPC_XRY_001-IMPC_XRY_034_001': 256,
           'MARC-IMPC_001-IMPC_XRY_001-IMPC_XRY_048_001': 257,
           'MRC Harwell-HRWLLA_001-HRWLLA_ECH_001-HRWLLA_ECH_025_001': 557,
           'MRC Harwell-HRWLLA_001-HRWLLA_XRY_001-HRWLLA_XRY_034_001': 569,
           'MRC Harwell-HRWLLA_001-HRWLLA_XRY_001-HRWLLA_XRY_048_001': 563,
           'MRC Harwell-HRWLLA_001-HRWLLA_XRY_001-HRWLLA_XRY_049_001': 577,
           'MRC Harwell-HRWLLA_001-HRWLLA_XRY_001-HRWLLA_XRY_050_001': 558,
           'MRC Harwell-HRWLLA_001-HRWLLA_XRY_001-HRWLLA_XRY_051_001': 570,
           'MRC Harwell-HRWL_001-IMPC_ABR_001-IMPC_ABR_014_001': 26,
           'MRC Harwell-HRWL_001-IMPC_ABR_002-IMPC_ABR_014_001': 27,
           'MRC Harwell-HRWL_001-IMPC_ALZ_001-IMPC_ALZ_076_001': 28,
           'MRC Harwell-HRWL_001-IMPC_ECH_001-IMPC_ECH_025_001': 29,
           'MRC Harwell-HRWL_001-IMPC_ELZ_001-IMPC_ELZ_064_001': 30,
           'MRC Harwell-HRWL_001-IMPC_XRY_001-IMPC_XRY_034_001': 31,
           'MRC Harwell-HRWL_001-IMPC_XRY_001-IMPC_XRY_048_001': 32,
           'MRC Harwell-HRWL_001-IMPC_XRY_001-IMPC_XRY_049_001': 33,
           'MRC Harwell-HRWL_001-IMPC_XRY_001-IMPC_XRY_050_001': 34,
           'MRC Harwell-HRWL_001-IMPC_XRY_001-IMPC_XRY_051_001': 35,
           'MRC Harwell-HRWL_001-IMPC_XRY_001-IMPC_XRY_052_001': 36,
           'NING-IMPC_001-IMPC_EYE_001-IMPC_EYE_050_001': 37,
           'NING-IMPC_001-IMPC_EYE_001-IMPC_EYE_051_001': 38,
           'NING-IMPC_001-IMPC_XRY_001-IMPC_XRY_034_001': 39,
           'NING-IMPC_001-IMPC_XRY_001-IMPC_XRY_048_001': 40,
           'RBRC-IMPC_001-IMPC_ALZ_001-IMPC_ALZ_076_001': 41,
           'RBRC-IMPC_001-IMPC_CSD_002-IMPC_CSD_085_001': 42,
           'RBRC-IMPC_001-IMPC_CSD_003-IMPC_CSD_085_001': 151,
           'RBRC-IMPC_001-IMPC_ELZ_001-IMPC_ELZ_064_001': 43,
           'RBRC-IMPC_001-IMPC_EYE_001-IMPC_EYE_050_001': 44,
           'RBRC-IMPC_001-IMPC_EYE_001-IMPC_EYE_051_001': 45,
           'RBRC-IMPC_001-IMPC_PAT_002-IMPC_PAT_057_002': 357,
           'RBRC-IMPC_001-IMPC_XRY_001-IMPC_XRY_034_001': 46,
           'RBRC-IMPC_001-IMPC_XRY_001-IMPC_XRY_048_001': 47,
           'RBRC-IMPC_001-IMPC_XRY_001-IMPC_XRY_049_001': 48,
           'RBRC-IMPC_001-IMPC_XRY_001-IMPC_XRY_050_001': 49,
           'RBRC-IMPC_001-IMPC_XRY_001-IMPC_XRY_051_001': 50,
           'RBRC-IMPC_001-IMPC_XRY_001-IMPC_XRY_052_001': 51,
           'RBRC-RBRCLA_001-RBRCLA_CSD_003-RBRCLA_CSD_085_001': 616,
           'RBRC-RBRCLA_001-RBRCLA_EYE_001-RBRCLA_EYE_050_001': 583,
           'RBRC-RBRCLA_001-RBRCLA_EYE_001-RBRCLA_EYE_051_001': 610,
           'RBRC-RBRCLA_001-RBRCLA_PAT_002-RBRCLA_PAT_057_002': 613,
           'TCP-TCPIP_001-TCPIP_CSD_003-TCPIP_CSD_085_001': 553,
           'TCP-TCPLA_001-TCPLA_CSD_003-TCPLA_CSD_085_001': 556,
           'TCP-TCPLA_001-TCPLA_ECG_002-TCPLA_ECG_025_001': 654,
           'TCP-TCPLA_001-TCPLA_ECG_003-TCPLA_ECG_025_001': 1051,
           'TCP-TCPLA_001-TCPLA_EYE_002-TCPLA_EYE_050_001': 567,
           'TCP-TCPLA_001-TCPLA_EYE_002-TCPLA_EYE_051_001': 571,
           'TCP-TCPLA_001-TCPLA_EYE_003-TCPLA_EYE_050_001': 854,
           'TCP-TCPLA_001-TCPLA_EYE_003-TCPLA_EYE_051_001': 805,
           'TCP-TCPLA_001-TCPLA_HIS_002-TCPLA_HIS_177_001': 755,
           'TCP-TCPLA_001-TCPLA_HIS_003-TCPLA_HIS_177_001': 851,
           'TCP-TCPLA_001-TCPLA_PAT_002-TCPLA_PAT_057_002': 552,
           'TCP-TCPLA_001-TCPLA_XRY_001-TCPLA_XRY_034_001': 559,
           'TCP-TCPLA_001-TCPLA_XRY_001-TCPLA_XRY_048_001': 566,
           'TCP-TCPLA_001-TCPLA_XRY_001-TCPLA_XRY_050_001': 564,
           'TCP-TCPLA_001-TCPLA_XRY_001-TCPLA_XRY_051_001': 560,
           'TCP-TCP_001-IMPC_ALZ_001-IMPC_ALZ_075_001': 52,
           'TCP-TCP_001-IMPC_ALZ_001-IMPC_ALZ_076_001': 53,
           'TCP-TCP_001-IMPC_CSD_002-IMPC_CSD_085_001': 54,
           'TCP-TCP_001-IMPC_CSD_003-IMPC_CSD_085_001': 258,
           'TCP-TCP_001-IMPC_ECG_002-IMPC_ECG_025_001': 454,
           'TCP-TCP_001-IMPC_ECG_003-IMPC_ECG_025_001': 1052,
           'TCP-TCP_001-IMPC_ELZ_001-IMPC_ELZ_063_001': 55,
           'TCP-TCP_001-IMPC_ELZ_001-IMPC_ELZ_064_001': 56,
           'TCP-TCP_001-IMPC_EYE_001-IMPC_EYE_050_001': 57,
           'TCP-TCP_001-IMPC_EYE_001-IMPC_EYE_051_001': 58,
           'TCP-TCP_001-IMPC_EYE_002-IMPC_EYE_050_001': 259,
           'TCP-TCP_001-IMPC_EYE_002-IMPC_EYE_051_001': 260,
           'TCP-TCP_001-IMPC_EYE_003-IMPC_EYE_050_001': 808,
           'TCP-TCP_001-IMPC_EYE_003-IMPC_EYE_051_001': 811,
           'TCP-TCP_001-IMPC_GEL_002-IMPC_GEL_044_001': 59,
           'TCP-TCP_001-IMPC_GEL_003-IMPC_GEL_044_001': 405,
           'TCP-TCP_001-IMPC_GEM_003-IMPC_GEM_049_001': 358,
           'TCP-TCP_001-IMPC_GEO_002-IMPC_GEO_050_001': 60,
           'TCP-TCP_001-IMPC_GEO_003-IMPC_GEO_050_001': 353,
           'TCP-TCP_001-IMPC_GEP_003-IMPC_GEP_064_001': 461,
           'TCP-TCP_001-IMPC_HIS_001-IMPC_HIS_177_001': 61,
           'TCP-TCP_001-IMPC_HIS_002-IMPC_HIS_177_001': 451,
           'TCP-TCP_001-IMPC_HIS_003-IMPC_HIS_177_001': 802,
           'TCP-TCP_001-IMPC_PAT_002-IMPC_PAT_057_002': 62,
           'TCP-TCP_001-IMPC_XRY_001-IMPC_XRY_034_001': 63,
           'TCP-TCP_001-IMPC_XRY_001-IMPC_XRY_048_001': 64,
           'TCP-TCP_001-IMPC_XRY_001-IMPC_XRY_050_001': 65,
           'TCP-TCP_001-IMPC_XRY_001-IMPC_XRY_051_001': 261,
           'TCP-TCP_001-IMPC_XRY_001-IMPC_XRY_052_001': 66,
           'UC Davis-UCDLA_001-UCDLA_XRY_001-UCDLA_XRY_034_001': 605,
           'UC Davis-UCDLA_001-UCDLA_XRY_001-UCDLA_XRY_048_001': 608,
           'UC Davis-UCD_001-IMPC_ALZ_001-IMPC_ALZ_075_001': 67,
           'UC Davis-UCD_001-IMPC_ALZ_001-IMPC_ALZ_076_001': 68,
           'UC Davis-UCD_001-IMPC_EML_001-IMPC_EML_001_001': 701,
           'UC Davis-UCD_001-IMPC_HIS_001-IMPC_HIS_177_001': 561,
           'UC Davis-UCD_001-IMPC_XRY_001-IMPC_XRY_034_001': 69,
           'UC Davis-UCD_001-IMPC_XRY_001-IMPC_XRY_048_001': 70,
           'UC Davis-UCD_001-IMPC_XRY_001-IMPC_XRY_049_001': 757,
           'UC Davis-UCD_001-IMPC_XRY_001-IMPC_XRY_050_001': 753,
           'UC Davis-UCD_001-IMPC_XRY_001-IMPC_XRY_051_001': 754,
           'UC Davis-UCD_001-IMPC_XRY_001-IMPC_XRY_052_001': 756,
           'WTSI-DSS_001-DSS_DSS_001-DSS_DSS_018_001': 407,
           'WTSI-MGP_001-IMPC_EYE_001-IMPC_EYE_050_001': 71,
           'WTSI-MGP_001-IMPC_EYE_001-IMPC_EYE_051_001': 72,
           'WTSI-MGP_001-IMPC_HIS_001-IMPC_HIS_177_001': 622,
           'WTSI-MGP_001-MGP_ALZ_001-MGP_ALZ_127_001': 615,
           'WTSI-MGP_001-MGP_ANA_001-MGP_ANA_005_001': 301,
           'WTSI-MGP_001-MGP_BMI_001-MGP_BMI_045_001': 501,
           'WTSI-MGP_001-MGP_EEI_001-MGP_EEI_114_001': 401,
           'WTSI-MGP_001-MGP_IMM_001-MGP_IMM_233_001': 503,
           'WTSI-MGP_001-MGP_MLN_001-MGP_MLN_206_001': 502,
           'WTSI-MGP_001-MGP_XRY_001-IMPC_XRY_034_001': 73,
           'WTSI-MGP_001-MGP_XRY_001-IMPC_XRY_048_001': 74,
           'WTSI-MGP_001-MGP_XRY_001-IMPC_XRY_049_001': 75,
           'WTSI-MGP_001-MGP_XRY_001-IMPC_XRY_050_001': 76,
           'WTSI-MGP_001-MGP_XRY_001-IMPC_XRY_051_001': 77,
           'WTSI-MGP_001-MGP_XRY_001-IMPC_XRY_052_001': 78,
           'JAX-JAX_001-JAX_ERG_003-JAX_ERG_028_001': 1400,
            'BCM-IMPC_001-IMPC_EYE_002-IMPC_EYE_051_001': 1500,
            'BCM-BCM_001-IMPC_EYE_003-IMPC_EYE_079_001': 1501,
            'BCM-BCM_001-IMPC_EYE_003-IMPC_EYE_075_001': 1502,
            'BCM-IMPC_001-IMPC_EYE_002-IMPC_EYE_050_001': 1503,
            'BCM-BCM_001-IMPC_EYE_003-IMPC_EYE_078_001': 1504,
            'BCM-BCM_001-IMPC_EYE_003-IMPC_EYE_074_001': 1505,
           'JAX-JAXLA_001-JAXLA_XRY_001-JAXLA_XRY_050_001': 1506,
           'JAX-JAXLA_001-JAXLA_XRY_001-JAXLA_XRY_034_001': 1507,
           'JAX-JAXLA_001-JAXLA_XRY_001-JAXLA_XRY_049_001': 1508,
           'BCM-BCM_001-IMPC_ECG_003-IMPC_ECG_025_001': 1509,
           'BCM-BCMLA_001-BCMLA_ECG_003-BCMLA_ECG_025_001': 1510,
           'UC Davis-UCD_001-IMPC_EMO_001-IMPC_EMO_001_001': 1511,
           'UC Davis-UCD_001-IMPC_HIS_003-IMPC_HIS_177_001': 1512,
           'BCM-BCM_001-IMPC_EML_001-IMPC_EML_001_001': 1513,
           'BCM-BCM_001-IMPC_EMO_001-IMPC_EMO_001_001': 1514,
           'JAX-JAX_001-IMPC_EMA_002-IMPC_EMA_001_001': 1515,
           'JAX-JAX_001-IMPC_EMA_001-IMPC_EMA_001_001': 1516,
           'TCP-TCP_001-IMPC_EMO_001-IMPC_EMO_001_001': 1517,
           'UC Davis-UCD_001-IMPC_GEO_003-IMPC_GEO_050_001': 1518,
           'UC Davis-UCD_001-IMPC_GEP_003-IMPC_GEP_064_001': 1519,
           'UC Davis-UCD_001-IMPC_GEL_003-IMPC_GEL_044_001': 1520,
           'KMPC-IMPC_001-IMPC_EYE_003-IMPC_EYE_051_001': 1521,
           'UC Davis-UCD_001-IMPC_EMA_001-IMPC_EMA_001_001': 1522,
           'TCP-TCP_001-IMPC_EMA_001-IMPC_EMA_001_001': 1523,
           'UC Davis-UCDLA_001-UCDLA_HIS_003-UCDLA_HIS_177_001': 1524,
           'TCP-TCP_001-IMPC_EOL_001-IMPC_EOL_001_001': 1525
           }

def main(argv):
    print "running main method in OmeroService"

    parser = argparse.ArgumentParser(
        description='Create an OmeroService object to interact with Omero'
    )
    parser.add_argument('-p', '--profile', dest='profile', default='dev',
                        help='profile to read omero properties from'
    )

    args = parser.parse_args()

    try:
        pp = OmeroPropertiesParser(args.profile)
        omeroProps = pp.getOmeroProps()
    except:
        omeroProps = {}
    
    omeroHost=omeroProps['omerohost']
    omeroPort=omeroProps['omeroport']
    omeroUsername=omeroProps['omerouser']
    omeroPass=omeroProps['omeropass']
    group=omeroProps['omerogroup']

    omeroS=OmeroService(omeroHost, omeroPort, omeroUsername, omeroPass, group)
    #directory_to_filename_map=omeroS.getImagesAlreadyInOmero("HMGU_HMGU_001")
    #print str(len(directory_to_filename_map))+" directories with images already in omero"
    #for key, value in directory_to_filename_map.items():
    #    print key +" "+str(value)
        
    #omeroS.loadFileOrDir("/nfs/komp2/web/images/impc/WTSI/MGP_001/MGP_XRY_001/IMPC_XRY_034_001/","WTSI-MGP_001", "IMPC_XRY_001-IMPC_XRY_034_001", ["97075.dcm"])
    omeroS.loadFileOrDir("/nfs/komp2/web/images/clean/impc/JAX/JAX_001/JAX_ERG_001/JAX_ERG_027_001/206931.pdf","MRC Harwell", "MRC Harwell-MGP_001-IMPC_XRY_001-IMPC_XRY_034_001")
    
class OmeroService:
    def __init__(self, omeroHost, omeroPort, omeroUsername, omeroPass, group):
        self.logger = logging.getLogger(__name__)
        self.logger.info("init OmeroService")
        self.omeroHost=omeroHost
        self.omeroPort=omeroPort
        self.omeroUsername=omeroUsername
        self.omeroPass=omeroPass
        self.group=group
        self.conn=self.getConnection()
        
    def getConnection(self):
        try: 
            self.cli = omero.cli.CLI()
            self.cli.loadplugins()
            self.cli.invoke(["login", self.omeroUsername+'@'+self.omeroHost, "-w", self.omeroPass, "-C"], strict=True)
            #cli.invoke(["login", "%s@localhost" % user, "-w", passw, "-C"], strict=True)
            self.cli.invoke(["sessions", "group", self.group], strict=True)
            sessionId = self.cli._event_context.sessionUuid
            self.conn = BlitzGateway(host=self.omeroHost)
            self.conn.connect(sUuid = sessionId)
    
            user = self.conn.getUser()
            #print "Current user:"
            #print "   ID:", user.getId()
            #print "   Username:", user.getName()
            #print "   Full Name:", user.getFullName()
    
            #print "Member of:"
            #for g in self.conn.getGroupsMemberOf():
                #print "   ID:", g.getId(), " Name:", g.getName()
            group = self.conn.getGroupFromContext()
            #print "Current group: ", group.getName()
    
            #print "Other Members of current group:"
            #for exp in conn.listColleagues():
            #    print "   ID:", exp.getId(), exp.getOmeName(), " Name:", exp.getFullName()
    
            #print "Owner of:"
            #for g in conn.listOwnedGroups():
            #    print "   ID:", g.getName(), " Name:", g.getId()
    
            # New in OMERO 5
            #print "Admins:"
            #for exp in conn.getAdministrators():
            #   print "   ID:", exp.getId(), exp.getOmeName(), " Name:", exp.getFullName()
    
            # The 'context' of our current session
            ctx = self.conn.getEventContext()
            # print ctx     # for more info
            #print ctx
            return self.conn
        except Exception, e:
            self.logger.exception(e)
    
    def getImagesAlreadyInOmero(self):
        query = 'SELECT clientPath FROM FilesetEntry WHERE fileset.id >= :id';
        params = omero.sys.ParametersI()
        params.addId(omero.rtypes.rlong(0))
        omero_file_data = self.conn.getQueryService().projection(query, params)

        # Get the filepath by splitting the indir path
        omero_file_list = []
        for ofd in omero_file_data:
            try:
                #indir,ofd_path = ofd[1].split(root_dir[1:])
                ofd_path = ofd[0].val.split('impc/')[-1]
            except Exception as e:
                self.logger.error("Problem extracting root_dir from clientpath " + ofd[0].val)
                self.logger.error("Error was: " + str(e))
                omero_file_list.append(ofd[0].val)
                continue
            #if indir is None or len(indir) < 1:
            #    print "Did not extract root_dir from " + ofd[1]
            omero_file_list.append(ofd_path)
        return omero_file_list

    def getImagesAlreadyInOmeroViaPostgres(self, omeroDbDetails):
        # Get files already in omero by directly querying postgres db
        try:
            print "Attempting to get file list directly from Postgres DB"
            omeroDbUser = omeroDbDetails['omerodbuser']
            omeroDbPass = omeroDbDetails['omerodbpass']
            omeroDbName = omeroDbDetails['omerodbname']
            omeroDbHost = omeroDbDetails['omerodbhost']
            omeroDbPort = omeroDbDetails['omerodbport']
        
            conn = psycopg2.connect(database=omeroDbName, user=omeroDbUser,
                                    password=omeroDbPass, host=omeroDbHost,
                                    port=omeroDbPort)
            cur = conn.cursor()
            # Get the actual files uploaded to Omero
            query = "SELECT DISTINCT clientpath FROM filesetentry " + \
                    "INNER JOIN fileset ON filesetentry.fileset=fileset.id " +\
                    "WHERE fileset.id >=0"
            cur.execute(query)
            omero_file_list = []
            for f in cur.fetchall():
                omero_file_list.append(f[0].split('impc/')[-1])
        
            ## Get the images contained in the leica files uploaded to Omero
            ## These images are in the download_urls obtained from solr
            #query = "SELECT name FROM image " + \
            #        "WHERE name LIKE '%.lif%' OR name LIKE '%.lei%'"
            #cur.execute(query)
            #omero_image_list = []
            #for i in cur.fetchall():
            #    omero_image_list.append(i[0])
            conn.close()
            return omero_file_list

        except KeyError as e:
            print "Could not connect to omero postgres database. Key " + str(e) + \
                  " not present in omero properties file. Aborting!"
            if 'conn' in locals():
                conn.close()
            sys.exit()


    def getAnnotationsAlreadyInOmero(self):
        #query = 'SELECT ds.name, (SELECT o.name FROM originalfile o ' + \
        #        'WHERE o.id=a.file) AS filename FROM datasetannotationlink ' + \
        #        'dsal, dataset ds, annotation a where dsal.parent=ds.id and ' + \
        #        ' dsal.child=a.id and ' + \
        #        ' dsal.child >= :id'
        
        omero_annotation_list = []
        file_annotations = self.conn.listFileAnnotations()
        for fa in file_annotations:
            links = fa.getParentLinks('dataset')
            for link in links:
                datasets = link.getAncestry()
                for ds in datasets:
                    dir_parts = ds.getName().split('-')
                    if len(dir_parts) == 4:
                        dir_parts.append(fa.getFileName())
                        omero_annotation_list.append("/".join(dir_parts))
        return omero_annotation_list


    def filterProjectFunction(self, project):
        self.logger.info("project name="+project.getName())
        if project.getName().startswith(self.filterProjectName):
            return True
        else:
            return False
    
    def loadFileOrDir(self, directory,  project=None, dataset=None, filenames=None):
        if filenames is not None:
            str_n_files = str(len(filenames))
        else:
            str_n_files = "0"

        self.logger.info("loadFileOrDir with: Directory=" + directory + ", project=" + str(project) + ", dataset=" + str(dataset) + ", # of files=" + str_n_files)
        #chop dir to get project and dataset
        
        #if filenames is non then load the entire dir
        if filenames is not None:
            for filename in filenames:
                fullPath=directory+"/"+filename
                self.logger.info("loading file="+fullPath)
                try:
                    self.load(fullPath, project, dataset)
                except Exception as e:
                    self.logger.warning("OmeroService Unexpected error loading file:" + str(e))
                    self.logger.warning("Skipping " + fullPath + " and continuing")
                    continue
                
        else:
            self.logger.info("loading directory")
            try:
                self.load(directory, project, dataset)
            except Exception as e:
                    self.logger.exception("OmeroService Unexpected error loading directory:" + str(e))

    def load(self, path, project=None, dataset=None):  
        self.logger.info("-"*10)
        self.logger.info("path="+path)
        self.logger.info("project="+str(project))
        self.logger.info("dataset="+str(dataset))
        #if self.cli is None or self.conn is None:
            #print "cli is none!!!!!"
        self.getConnection()
            
       
        import_args = ["import"]
        if project is not None:
            self.logger.info("project in load is not None. Project name: "+project)
            #project=project.replace(" ","-")
        if dataset is not None:
            self.logger.info("dataset in load is not None. Dataset name: "+dataset)

            ## Introducing a hack to see if the actual upload works
            dsId = DS_DICT[dataset]
            self.logger.info("DatasetId (first try) ="+str(dsId))
            if not dsId:
                dsId = DS_DICT[dataset.upper()]
                self.logger.info("DatasetId (second try) =" + str(dsId))
                if not dsId:
                    dsId = self.create_containers(self.cli, dataset, self.omeroHost, project)

            self.logger.info("datasetId="+str(dsId))
            import_args.extend(["--","-d", str(dsId), "--exclude","filename"])#"--no_thumbnails",,"--debug", "ALL"])
            #import_args.extend(["--","--transfer","ln_s","-d", str(dsId), "--exclude","filename"])#"--no_thumbnails",,"--debug", "ALL"])
            #import_args.extend(["--", "-d", str(dsId)])#"--no_thumbnails",,"--debug", "ALL"])
        else:
            self.logger.warning("dataset is None!!!!!!!!!!!!!!!!!!!!")
        
        self.logger.info('importing project=' + str(project) +  ', dataset=' + str(dataset) + ', filename=' + str(path))
        
        if(path.endswith('.pdf')):
            self.logger.info("We have a pdf document- loading as attachment "+str(path))#we need to upload as an attachment
            namespace = "imperial.training.demo"
            fileAnn = self.conn.createFileAnnfromLocalFile(str(path), mimetype=None, ns=namespace, desc=None)
            self.logger.info("fileAnn="+str(fileAnn))
            datasetForAnnotation = self.conn.getObject("Dataset", dsId)
            self.logger.info( "Attaching FileAnnotation to Dataset: " + str(datasetForAnnotation) + ", File ID: " + str(fileAnn.getId()) + ", File Name: " + fileAnn.getFile().getName() + ", Size:" + str(fileAnn.getFile().getSize()))
            self.logger.info("Dataset="+str(datasetForAnnotation))
            datasetForAnnotation.linkAnnotation(fileAnn)
            self.logger.info("linked annotation!")
        else:
            import_args.append(path)
            #print " import args="
            #print import_args
            self.cli.invoke(import_args, strict=True)
        self.conn._closeSession()
        #print "-" * 100
        
    def create_containers(self, cli, dataset, omeroHost, project=None):
        """
        Creates containers with names provided if they don't exist already.
        Returns Dataset ID.
        """
        #print 'create containers method called'
        params = omero.sys.Parameters()
        params.theFilter = omero.sys.Filter()
        params.theFilter.ownerId = wrap(self.conn.getUser().getId())
        #print "ownerId="+conn.getUser().getId()
        #project=None
        from omero.rtypes import rstring
        d = None
        prId = None
        if project is not None:
            p = self.conn.getObject("Project", attributes={'name': project}, params=params)
            if p is None:
                self.logger.info("Creating Project:" + project)
                p = omero.model.ProjectI()
                p.name = wrap(str(project))
                prId = self.conn.getUpdateService().saveAndReturnObject(p).id.val
                self.logger.info("Project id after created="+str(prId))
            else:
                self.logger.info( "Using Project:" + project + ":" + p.getName())
                prId = p.getId()
                # Since Project already exists, check children for Dataset
#                for c in p.listChildren():
#                    self.logger.info("c getname="+c.getName())
#                    if c.getName() == dataset:
#                        self.logger.info("c=d matches name")
#                        d = c
    

        if d is None:
            self.logger.info( "Creating Dataset:" + dataset)
            d = omero.model.DatasetI()
            d.name = wrap(str(dataset))
            dsId = self.conn.getUpdateService().saveAndReturnObject(d).id.val
            if prId is not None:
                self.logger.info("Linking Project-Dataset...")
                link = omero.model.ProjectDatasetLinkI()
                link.child = omero.model.DatasetI(dsId, False)
                link.parent = omero.model.ProjectI(prId, False)
                self.conn.getUpdateService().saveObject(link)

            self.logger.info("Created Dataset:" + dataset + ":" + dsId)
        else:
            self.logger.info( "Using Dataset:" + dataset + ":" + d.getName())
            dsId = d.getId()
        
        return dsId
    
    def print_obj(self, obj, indent=0):
        """
        Helper method to display info about OMERO objects.
        Not all objects will have a "name" or owner field.
        """
        msg = """%s%s:%s  Name:"%s" (owner=%s)""" % (\
                " " * indent,
                obj.OMERO_CLASS,\
                obj.getId(),\
                obj.getName(),\
                obj.getOwnerOmeName())
        self.logger.info(msg)
    
if __name__ == "__main__":
    main(sys.argv[1:]) 
