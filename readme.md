This program is to generate all sequence diagrams into .jpg files. 
It also generates some LaTeX code for the photo files.
We will need to revise the LaTeX code before using it.

The program requires to have 64-bit Rhapsody application installed on the computer.

We need to open the .rpy file in Rhapsody before running the file run.bat.

The photo files of the diagrams and the LaTeX code for the diagrams will be generated and put into a folder in the place where you run the file run.bat.
The name of the folder is the timestamp in the format yyyyMMddhhmmss.

You can specify the folder where the photos and the latex code are stored.
In order to do so, you need to edit the file run.bat and provide an argument when you run the jar file.
It needs to be the absolute path of the folder where you want to store the files.

For example, the below statement is to write all photos and latex file into the folder "C:\Users\huynhd\myfolder"
"C:\Program Files\IBM\Rational\Rhapsody\8.2.1\jdk\bin\java" -Djava.library.path=%userprofile%/.rhapsodyprocessor -jar RhapsodyProcessor-1.0.0.jar "C:\Users\huynhd\myfolder"

If you open several instances of Rhapsody for different .rpy files, the application will generate all sequences diagrams in the .rpy files.
In that case, you will have a LaTeX file for each .rpy file.