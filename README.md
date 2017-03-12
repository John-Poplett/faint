Faint
=====
The Face Annotation Interface

Background
----------
The Faint application was originally written by Malte Mathiszig as a Java
application. Initially developed in the context of a Bachelor Thesis at the
University of Oldenburg, faint has been integrated into several projects
maintained by the OFFIS Institute for Information Technology. To attract a
broader audience, the source code has been released under GNU General Public
License (GPL) in October 2007 and hosted at http://faint.sourceforge.net/.

What is this?
-----------
This is a fork of Malte Mathiszig's original sourceforge project brought over
to github and revised to use javacv for the interface to FAINT's OpenCV HaarCascade
face detector plugin. This change eliminates a dependency on Windows and should
make the plugin more OS-neutral.

This fork also has an FDDBExporter to export annotations from the FAINT database
in standard FDDB format.

Current Development
-------------------
This version has been tested on Sierra MacOS.

Original License
----------------

/*******************************************************************************
 * + -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- +
 * |                                                                         |
 *    faint - The Face Annotation Interface
 * |  Copyright (C) 2007  Malte Mathiszig                                    |
 *
 * |  This program is free software: you can redistribute it and/or modify   |
 *    it under the terms of the GNU General Public License as published by
 * |  the Free Software Foundation, either version 3 of the License, or      |
 *    (at your option) any later version.                                    
 * |                                                                         |
 *    This program is distributed in the hope that it will be useful,
 * |  but WITHOUT ANY WARRANTY; without even the implied warranty of         |
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * |  GNU General Public License for more details.                           |
 *
 * |  You should have received a copy of the GNU General Public License      |
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * |                                                                         |
 * + -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- +
 *******************************************************************************/

Thanks
======
Special thanks to Malte Mathiszig for releasing an amazing application.
