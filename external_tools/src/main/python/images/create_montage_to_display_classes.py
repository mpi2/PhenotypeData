# Script to create montages of images of the same class split by threshold.
# This script accepts an "all structures model" followed by one or more
# single structure models as well as a threshold for accepting 
# classifications
#
# It checks that the classifications of all models are consistent and 
# above the threshold and writes the output into one of three subfolders:
#       meets_threshold_and_consistent
#       less_than_threshold_and_consistent
#       inconsistent
#
import sys
import os
import argparse
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import SimpleITK as sitk

plt.ioff()

parser = argparse.ArgumentParser(
    description = "Create montages to allow manual verification of deep learning model classifications"
)
parser.add_argument(
    '--input-dir', dest='input_dir', default='',
    help='optional common base directory for input files'
)
parser.add_argument(
    '-i', '--input-file', dest='input_file', required=True,
    help='Paths to files with image paths and class labels. First must be all_structures_model'
)
parser.add_argument(
    '-l', '--label-column', dest='label_column', default='classlabel',
    help='Name of column where class labels are stored'
)
parser.add_argument(
    '-o', '--output-dir', dest='output_dir', default='./',
    help='Path to base directory to save the montages. Subfolders will be created here'
)
parser.add_argument(
    '-r', '--nrows', dest='n_rows', default=12, type=int,
    help='Number of rows in montage'
)
parser.add_argument(
    '-c', '--ncols', dest='n_cols', default=10, type=int,
    help='Number of columns in montage'
)
parser.add_argument(
    '-t', '--threshold', dest='threshold', default=0.99, type=float,
    help='Threshold between 0 and 1 for accepting classification'
)
#all_structures = pd.read_csv('./wtsi_image_class_list.txt')

args = parser.parse_args()

# Get the files to process
input_paths = [os.path.join(args.input_dir, input_file) for input_file in args.input_file.split(",")]

# Load details from all structures model
all_structures = pd.read_csv(input_paths[0])
n_rows = len(all_structures)

single_structures = {}

# If more than one model - the others are single structure models. Get
# labels from names of the single structure models
structure_label_map = {
    "head_dorsal": 1,
    "forepaw": 2,
    "whole_body_dorsal": 3,
    "whole_body_lateral": 4,
    "head_lateral": 5,
    "hind_leg_hip": 6,
}
for input_path in input_paths[1:]:
    for structure in structure_label_map.keys():
        if input_path.find(structure) >= 0:
            class_label = structure_label_map[structure]
            if class_label in single_structures.keys():
                print(f"class_label for positive examples ({class_label}) in {input_path} has already been used. Exiting")
                sys.exit(-1)
            single_structures[class_label] = pd.read_csv(input_path)

                
            
#for f in input_paths[1:]:
#    df = pd.read_csv(f)
#    if len(df) != n_rows:
#        print("Did not get expected number of rows ({n_rows}) from file {f}. Number of rows should be same as all structures model ({input_paths[0]}). Exiting")
#    classes = df[label_column]
#    class_labels = np.array(list(set(classes)))
#    class_index = np.nonzero(class_labels >= 0)[0]
#    if len(class_index) != 1:
#        print(f"File {f} does NOT contain exactly one label greater than zero. Labels = {class_labels}. Exiting")
#        sys.exit(-1)
#    class_label = class_labels[class_index[0]]
#    if class_label in single_structures.keys():
#        print(f"class_label for positive examples ({class_label}) in {f} has already been used. Exiting")
#        sys.exit(-1)
#    single_structures[class_label] = df.copy()

label_column = args.label_column
classes = all_structures[label_column]
class_labels = list(set(classes))
n_labels = len(class_labels)

output_dir = args.output_dir
if not os.path.isdir(output_dir):
    os.mkdir(output_dir)

sort_column = ['classscore', 'index',]

for label in class_labels:
    df_class = all_structures.loc[all_structures[label_column]==label]
    #if sort_column is not None:
    #    df_class = df_class.sort_values(by=sort_column, axis='index')
    df_class = df_class.rename_axis('index').sort_values(by=sort_column, axis='index')

    print("Number of images of class: " + str(label) + " = " + str(len(df_class)))
    # We do not want to process the label -1 as it is for unreadable images
    if label < 0:
        print("Not processing images of this class as its label is less than 0")
        continue

    # Sort the images into three categories for outputting
    #       1) meets_threshold_and_consistent
    #       2) less_than_threshold_and_consistent
    #       3) inconsistent
    meets_threshold_and_consistent = []
    less_than_threshold_and_consistent = []
    inconsistent = []
    for counter, (i, row) in enumerate(df_class.iterrows()):
        # Assume image is consistent
        inconsistent_flag = False
        less_than_threshold_flag = row[sort_column[0]] < args.threshold
        for structure_label, df in single_structures.items():
            # Check file paths are the same
            if row['imagename'] != df.iloc[i]['imagename']:
                print(f"Values for imagepath for index {i} are different in all structures model csv and csv for single structure model for label {label}. Values are {row['imagename']} vs {df.iloc[i]['imagename']}. Exiting!")
                sys.exit(-1)
            # Check if consistent with single structure models
            if label == structure_label and df.iloc[i][label_column] != label:
                inconsistent_flag = True
                break
            elif label != structure_label and df.iloc[i][label_column] >=0:
                inconsistent_flag = True
                break

            # Check if less than threshold
            if df.iloc[i][sort_column[0]] < args.threshold:
                less_than_threshold_flag = True

        # Assign to inconsistent category if inconsistent flag is True
        if inconsistent_flag:
            inconsistent.append(i)
        else:
            if less_than_threshold_flag:
                # Assign to consistent less than threshold if less than
                # threshold flag is True
                less_than_threshold_and_consistent.append(i)
            else:
                # Assign to consistent meets threshold
                meets_threshold_and_consistent.append(i)


    # Create montages of 12 x 10
    n_rows = args.n_rows
    n_cols = args.n_cols
    n_subplots = n_rows * n_cols

    dict_to_process = {}
    if len(meets_threshold_and_consistent) > 0:
        dict_to_process['meets_threshold_and_consistent'] = meets_threshold_and_consistent
    if len(less_than_threshold_and_consistent) > 0:
        dict_to_process['less_than_threshold_and_consistent'] = less_than_threshold_and_consistent
    if len(inconsistent) > 0:
        dict_to_process['inconsistent'] = inconsistent
        
    
    for category, to_process in dict_to_process.items():

        fig = plt.figure()
        counter = -1
        df_class = all_structures.iloc[to_process]
        output_dir_prefix = os.path.join(output_dir, category)
        if not os.path.isdir(output_dir_prefix):
            os.mkdir(output_dir_prefix)
        for counter, (i, row) in enumerate(df_class.iterrows()):
            if counter > 0 and counter%n_subplots == 0:
                fig_path = "class_{0}_{1:06d}.png".format(label,counter)
                fig_path = os.path.join(output_dir_prefix, fig_path)
                plt.savefig(fig_path)
                print(f"Saved figure to {fig_path}")
                plt.clf()
            counter1 = (counter%n_subplots) + 1
            ax = plt.subplot(n_rows,n_cols,counter1)
            im_path = row['imagename']

            # Prevent crash if image cannot be read
            try:
                im = sitk.ReadImage(im_path)
            except Exception as e:
                print("Could not read " + im_path + ". Error was:")
                print(e)
                continue
            im = sitk.GetArrayFromImage(im)
            im = np.squeeze(im)
            ax.imshow(im)
            if sort_column is not None:
                title = "{0:>5d} ({1:1.2f})".format(i, row[sort_column[0]])
                ax.set_title(title, {'fontsize': 5},pad=0.0)
            else:
                ax.set_title(str(i),{'fontsize': 5},pad=0.0)
            ax.axis('off')

        # Plot any remaining figures
        if counter >= 0:
            fig_path = "class_{0}_{1:06d}.png".format(label,counter)
            fig_path = os.path.join(output_dir_prefix, fig_path)
            plt.draw()
            plt.savefig(fig_path)
            print(f"Saved figure to {fig_path}")
        plt.clf()
