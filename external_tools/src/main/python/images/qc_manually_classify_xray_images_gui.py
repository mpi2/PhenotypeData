"""
    GUI to manually classify mouse x-ray images
"""

import sys
import os
import argparse

from collections import defaultdict, OrderedDict
from io import StringIO

import numpy as np
import matplotlib.pyplot as plt
from matplotlib.widgets import  Button, RadioButtons, CheckButtons
import SimpleITK as sitk

# Number of classes (as of 04/03/2019):
#  0) unclassified
#  1) head (dorsoventral - IMPC_XRY_051_001)
#  2) right paw (dorsoventral - IMPC_XRY_049_001)
#  3) whole mouse (dorsoventral - IMPC_XRY_034_001)
#  4) whole mouse (lateral - IMPC_XRY_048_001)
#  5) head (lateral - IMPC_XRY_050_001)
#  6) hind leg (dorsoventral - IMPC_XRY_052_001)
#  7) other
dict_image_class = OrderedDict({
    'unclassified': '0',
    'head (dorsoventral)': '1',
    'right paw': '2',
    'whole mouse (dorsoventral)': '3',
    'whole mouse (lateral)': '4',
    'head (lateral)': '5',
    'hind leg': '6',
    'other': '7',
})
NUM_CLASSES = len(dict_image_class.keys())

parser = argparse.ArgumentParser(
    description="Run GUI to allow manual classification of mouse xray images"
)
parser.add_argument('-i', '--in-filepath', dest='inFilepath',required=True,
                    help="File containing path to images to open and class assigned"
)
parser.add_argument('--indir-base', dest='indirBase',
                    help="Base directory to append to image paths"
)
parser.add_argument('-s', '--startIndex', dest='startIndex', default=0,
                    type=int, help="Index to start at (0 based)"
)

args = parser.parse_args()
in_filepath = args.inFilepath
indir_base = "" if args.indirBase is None else args.indirBase

with open(in_filepath,'rt') as fid:
    image_class_list = [line.strip('\n').split(',') for line in fid.readlines()[1:]]
nimages = len(image_class_list)
def onselect(eclick, erelease):
  'eclick and erelease are matplotlib events at press and release'
  print(' used button   : ', eclick.button)

class Index(object):
    ind = args.startIndex
    ax = plt.gca()
    aximage = None
    #nimages = len(mag_recs)
    nimages_minus_1 = nimages - 1
    str_nimages = str(nimages)

    def set_radio(self,radio):
        self.radio = radio

    def set_check(self,check):
        self.check = check
        self.set_auto_next_image()

    def next(self, event):
        self.ind += 1
        ax.set_title('Iteration ' + str(self.ind))
        plt.show()

    def prev(self, event):
        self.ind -= 1
        ax.set_title('Iteration ' + str(self.ind))
        plt.show()

    def showNextImage(self, event=None):
        if self.ind < self.nimages_minus_1:
            self.ind += 1 
        self.show_image()

    def showPrevImage(self, event):
        if self.ind > 0:
            self.ind -= 1
        self.show_image()

    def saveToFile(self, event):
        """
            Save contents of the classification list to file
        """
        output = "imagename,classlabel\n"
        for line in image_class_list:
            output += ",".join(line)
            output += "\n"
        with open(in_filepath, 'wt') as fid:
            fid.write(output)
        print("saved output to ", in_filepath)
        
    def process_click(self, event, name):
        #button = buttons[name]
        self.ind += 1
        ax.set_title('button clicked ' + str(self.ind))
        #print(event)
        plt.show()

    def show_image(self):
        print(self.ind)
        im_path = image_class_list[self.ind][0]
        im = sitk.ReadImage(im_path)
        im = sitk.GetArrayFromImage(im)
        im = np.squeeze(im)
        if self.ax is None:
           self.ax = plt.sublot(111)
        if self.aximage is None:
            self.aximage = self.ax.imshow(im)
            #self.aximage.set_axis_off()
        else:
            self.aximage.set_data(im)
            # set extents as (xmin,xmax,ymax,ymin) so y axis origin at topleft
            self.aximage.set_extent((0,im.shape[1],im.shape[0],0))
            
        self.set_title()
        self.im_width = im.shape[1]
        self.im_height = im.shape[0]
        
        # Set the class (radio button) if it already exists in list
        self.radio.eventson = False
        if len(image_class_list[self.ind]) > 1:
            im_class = image_class_list[self.ind][1]
            #print( "Image is of class " + str(im_class))
            self.radio.set_active(int(im_class))
        else:
            #print("Image not yet classified")
            self.radio.set_active(0)
        #try:
        self.radio.eventson = True

        #ipdb.set_trace()
        #self.draw_rect()
        plt.draw()

    def set_title(self):
        title = "X-ray image classification - manual"
        self.ax.set_title(title, fontsize=8)

    def set_image_class(self, event):
        #print("Setting image class to " + event + ": " + dict_image_class[event])
        try:
            image_class_list[self.ind][1] = dict_image_class[event]
        except IndexError as e:
            image_class_list[self.ind].append(dict_image_class[event])
        #print(image_class_list[self.ind])
        if self.auto_next_image:
            self.showNextImage()

    def set_auto_next_image(self, label=None):
        self.auto_next_image = self.check.get_status()[0]
    

def toggle_selector(event):
    print(' Key pressed.')
    if event.key in ['Q', 'q'] and toggle_selector.ES.active:
        print(' EllipseSelector deactivated.')
        toggle_selector.RS.set_active(False)
    if event.key in ['A', 'a'] and not toggle_selector.ES.active:
        print(' EllipseSelector activated.')
        toggle_selector.ES.set_active(True)


fig = plt.figure
plt.gray()
global ax
ax = plt.subplot(111)

callback = Index()

left = 0.85
bottom = 0.05
width = 0.1
height = 0.075
offset = 0.01
buttons = {
    'showNextImage': {'index': 3, 'callback': callback.showNextImage}, 
    'showPrevImage': {'index': 2, 'callback': callback.showPrevImage},
    'saveToFile': {'index': 1, 'callback': callback.saveToFile}
}

for key in buttons.keys():
    button = buttons[key]
    bottom2 = bottom + button['index'] * (height + offset)
    button['axhandle'] = plt.axes([left, bottom2, width, height])
    button['handle'] = Button(button['axhandle'], key)
    button['handle'].on_clicked(button['callback'])
    
# Radiobuttons
#left = 0.05
bottom = 0.45
width = 0.15
height = 0.2
offset = 0.01

rax = plt.axes([left,bottom,width,height])
radio = RadioButtons(rax, list(dict_image_class.keys()))
radio.on_clicked(callback.set_image_class)

callback.set_radio(radio)

# Checkbox for advancing to next image automatically
bottom = 0.75
rax = plt.axes([left,bottom,width,height])
check = CheckButtons(rax, ('auto next',), (False,))
check.on_clicked(callback.set_auto_next_image)
callback.set_check(check)

callback.show_image()
plt.show()

