"""Configure Omero on a k8s server for the way it is used in IMPC

"""
import sys
import argparse

import omero.cli # For some reason if I do not do this next line throws error
from omero import ApiUsageException
from omero.cli import NonZeroReturnCode

from omeroservice import OmeroService
from utils import get_properties_from_configuration_file

argument_parser = argparse.ArgumentParser(
    description="Configure Omero on a k8s server for use in IMPC"
)
argument_parser.add_argument("--omero-props-path", required=True,
    help="Path to configuration file"
)

args = argument_parser.parse_args()

omero_props = get_properties_from_configuration_file(args.omero_props_path)
omero_host = omero_props['omerohost']
omero_port = omero_props['omeroport']
omero_root_user = omero_props['omerouser']
omero_root_pass = omero_props['omeropass']
omero_public_user = omero_props['omeropublicuser']
omero_public_group = omero_props['omerogroup']
omero_public_pass = omero_props['omeropublicpass']
omero_system_group = omero_props['omerosystemgroup']

omero_service = OmeroService(omero_host, omero_port, omero_root_user, omero_root_pass, omero_system_group)
cli = omero_service.cli

def run_command(cli, cmd):
    """Run a command in the omero cli. Exit if error

    """
    try:
        cli.invoke(cmd, strict=True)
    except (ApiUsageException, NonZeroReturnCode,) as e:
        print(f"Error running command {cmd}.\nError was: {e}")
        print("\nExiting")
        sys.exit(-1)

# Create the public group
cmd = f"group add --type read-only --ignore-existing {omero_public_group}"
run_command(cli, cmd)
    
# Create the public user
cmd = f"user add --ignore-existing -i EBI " + \
      f"--group-name {omero_public_group} -P {omero_public_pass} " + \
      f"{omero_public_user} public user"
run_command(cli, cmd)

# Ensure the webclient allows login by the public user without any password

# Ensure the root user is part of the public group - so can import/export images
cmd = f"user joingroup --name {omero_public_user} --group-name {omero_public_group} --as-owner"
run_command(cli, cmd)
