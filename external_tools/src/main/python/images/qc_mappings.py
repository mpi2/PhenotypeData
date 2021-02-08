"""Mappings for entities that used by scripts in this directory

"""

# Phenotype centres in Solr have spaces, but in some of the code the spaces
# removed. This maps from 
SITES_SPACES_TO_NOSPACES_MAP = {
    'UC Davies': 'UCD',
    'MRC Harwell': 'HRWL',
}

SITES_NOSPACES_TO_SPACES_MAP = {
    'UCD': 'UC Davis',
    'HRWL': 'MRC Harwell',
}

PARAMETER_ID_TO_CLASS_MAP = {
    '034': 3,   # whole_body_dorsal
    '048': 4,   # whole_body_lateral
    '049': 2,   # forepaw
    '050': 5,   # head_lateral
    '051': 1,   # head_dorsal
    '052': 6,   # hind_leg_hip
}

CLASS_TO_PARAMETER_ID_MAP = {
    1: '051',   # head_dorsal
    2: '049',   # forepaw
    3: '034',   # whole_body_dorsal
    4: '048',   # whole_body_lateral
    5: '050',   # head_lateral
    6: '052',   # hind_leg_hip
}

STRUCTURE_TO_LABEL_MAP = {
    "head_dorsal": 1,
    "forepaw": 2,
    "whole_body_dorsal": 3,
    "whole_body_lateral": 4,
    "head_lateral": 5,
    "hind_leg_hip": 6,
    "unreadable_image": -1,
    "uncategorisable_image": -2
}

LABEL_TO_STRUCTURE_MAP = {
    1: "head_dorsal",
    2: "forepaw",
    3: "whole_body_dorsal",
    4: "whole_body_lateral",
    5: "head_lateral",
    6: "hind_leg_hip",
    -1: "unreadable_image",
    -2: "uncategorisable_image"
}

def sites_spaces_to_nospaces_map(site):
    """Map site with spaces to site with no spaces.

    """
    if site.count(" ") == 0:
        return site
    else:
        return SITES_SPACES_TO_NOSPACES_MAP[site]

def sites_nospaces_to_spaces_map(site):
    """Map site with no spaces to site with spaces.

    """
    try:
        return SITES_NOSPACES_TO_SPACES_MAP[site]
    except KeyError:
        return site

def parameter_id_to_class_map(pid):
    """Return class for parameter stable ID"""

    # Assumes parameter ID of form *_XRY_PID_001
    key = pid.split("_")[2]
    return PARAMETER_ID_TO_CLASS_MAP[key]

def class_to_parameter_id_map(class_label, prefix=""):
    """Return parameter stable ID for class"""
    
    pid = CLASS_TO_PARAMETER_ID_MAP[class_label]
    # if prefix supplied form whole parameter stable ID (PREFIX_XRY_PID_001)
    if prefix != "":
        return "_".join([prefix, "XRY", pid, "001"])
    else:
        return pid
