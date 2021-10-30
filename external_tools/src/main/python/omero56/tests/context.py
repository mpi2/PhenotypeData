"""Allow explicit path modification to resolve importing code to be tested

Followed suggestion from https://docs.python-guide.org/writing/structure/
"""

from pathlib import Path
import sys
sys.path.insert(0, str(Path(__file__).parent.joinpath("..").absolute()))

import scripts
