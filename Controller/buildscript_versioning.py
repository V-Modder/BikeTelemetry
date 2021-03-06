FILENAME_BUILDNO = 'versioning'
FILENAME_INCLUDE_PATH = 'include'
FILENAME_VERSION_H = FILENAME_INCLUDE_PATH + '/version.h'
version = 'v0.1.'

import datetime
import os

build_no = 0
try:
    with open(FILENAME_BUILDNO) as f:
        build_no = int(f.readline()) + 1
except:
    print('Starting build number from 1..')
    build_no = 1
with open(FILENAME_BUILDNO, 'w+') as f:
    f.write(str(build_no))
    print('Build number: {}'.format(build_no))

hf = """
#ifndef BUILD_NUMBER
  #define BUILD_NUMBER "{}"
#endif
#ifndef VERSION
  #define VERSION "{} - {}"
#endif
#ifndef VERSION_SHORT
  #define VERSION_SHORT "{}"
#endif
""".format(build_no, version+str(build_no), datetime.datetime.now(), version+str(build_no))

if not os.path.isfile(FILENAME_INCLUDE_PATH):
    os.mkdir(FILENAME_INCLUDE_PATH)
with open(FILENAME_VERSION_H, 'w+') as f:
    f.write(hf)