# Script to create montages of images of the same class
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
    '-i', '--input-file', dest='input_path', required=True,
    help='Path to file with image paths and class labels'
)
parser.add_argument(
    '-l', '--label-column', dest='label_column', default='classlabel',
    help='Name of column where class labels are stored'
)
parser.add_argument(
    '-o', '--output-dir', dest='output_dir', default='./',
    help='Path to directory to save the montages'
)
parser.add_argument(
    '-r', '--nrows', dest='n_rows', default=12, type=int,
    help='Number of rows in montage'
)
parser.add_argument(
    '-c', '--ncols', dest='n_cols', default=10, type=int,
    help='Number of columns in montage'
)
#im_details = pd.read_csv('./wtsi_image_class_list.txt')

args = parser.parse_args()
im_details = pd.read_csv(args.input_path)
classes = im_details[args.label_column]
class_labels = list(set(classes))
n_labels = len(class_labels)

output_dir = args.output_dir
if not os.path.isdir(output_dir):
    os.mkdir(output_dir)


for label in class_labels:
    ind = np.nonzero(classes == label)
    print("Number of images of class: " + str(label) + " = " + str(len(ind[0])))
    # We do not want to process the label -1 as it is for unreadable images
    if label < 0:
        print("Not processing images of this class as its label is less than 0")
        continue

    # Create montages of 12 x 10
    n_rows = args.n_rows
    n_cols = args.n_cols
    n_subplots = n_rows * n_cols
    fig = plt.figure()
    for counter,i in enumerate(ind[0]):
        if counter > 0 and counter%n_subplots == 0:
            fig_path = "class_{0}_{1:06d}.png".format(label,counter)
            fig_path = os.path.join(output_dir, fig_path)
            plt.savefig(fig_path)
            print(f"Saved figure to {fig_path}")
            plt.clf()
        counter1 = (counter%n_subplots) + 1
        ax = plt.subplot(n_rows,n_cols,counter1)
        im_path = im_details['imagename'][i]

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
        ax.set_title(str(i),{'fontsize': 5},pad=0.0)
        ax.axis('off')
    fig_path = "class_{0}_{1:03d}.png".format(label,counter)
    fig_path = os.path.join(output_dir, fig_path)
    plt.draw()
    plt.savefig(fig_path)
    print(f"Saved figure to {fig_path}")
    plt.clf()
