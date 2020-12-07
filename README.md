#### The aim of this project is to create a program which will automatically upload zoom video files to Box.com.

## Background

I currently work as a tutor for my university, tutoring several subjects including calculus, Java, and 
general algorithms and data structures. Due to the pandemic, all the tutoring is conducted via Zoom. 
My university requires tutors to upload video recordings of our tutoring sessions to Box.com. We must 
manually find and rename the video files (according to the format 'Name - Date - Position'), 
and then upload them to Box.com. This creates several issues; tutors forget to upload their files, tutors 
make typos in filenames, and tutors must do extra work, especially those who do many sessions a week. 
To solve these issues, and to help my fellow tutors, I decided to write a program to automate this process.

## Implementation

The program is written in Java. The first time it is opened, it asks the user for several details.

- Their name in the format 'FirstName LastInitial'
- Their position in the format 'STC PositionName' (the main positions are 'Tutoring' and 'Study Group')
- The starting date for uploads
- The location of their Zoom folder

The program asks for this information because Zoom files must be renamed with the format 
'Name - Date - Position'. The Zoom folder is needed in order for the program to search that folder for video files.
All info is stored in 'user.json'.

Once it obtains all the required information, the program is ready to run! The program will then ask the user 
if they would like to start the file upload. It will do this on every subsequent run of the program, since it 
no longer needs to ask for user info. If the user says 'yes', the program will parse all files in the given Zoom 
folder, and search for tutoring videos. Because Zoom names recording folders based on the Zoom meeting name, 
and all tutors are required to have 'CAS Tutoring' in their meeting name, the program is able to easily find 
the folders related to tutoring, and collect the .mp4 files from those folders. Then, the program connects to 
Box.com's API and uploads the files. Once this is complete it will notify the user.

## Future Plans

Right now the program is connected to my personal Box.com account, and uses a dev token for authentication. 
In the future I hope to connect it to the Box.com folder that stores all the tutoring video files, and set up 
authentication for individual users so that anyone can use it. I also want to add support for study group leaders, 
since right now the program only works for tutors.
