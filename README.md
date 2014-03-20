kobosync
========

A standalone Java app to copy data from Android to local computer and aggregate survey records into a CSV file


Introduction
============
This app syncs all completed forms from whatever directory you point it to into a directory of your choosing to store files from multiple phones into a single location on the computer on which the app is run. It is recursive so it doesn't worry that ODK puts all the forms into separate folders, you just point it at the top level folder. The XML storage directory will be populated with surveys taken from the XML source directory. The individual surveys are renamed based on the survey instance name, DeviceID of the phone used to collect the data, and the time at which the survey was started to the millisecond. This combination of data is used as a unique key throughout the process of backing up and transcribing the surveys into CSV and allows surveys from multiple phones to be collected into one location without having to worry about losing or overwriting existing data.

From the storage directory, it then aggregates all the records into a single CSV file, and places that file in a directory of your choosing. The application uses the instance name, DeviceID, & Start time data combination to create a unique key for each record within the CSV file. It is smart enough to handle changes in the schema over time, so that if you add a question to your survey, it won't trash the sync.

The Java app is attached, it has to be run from the command line, but it does have a GUI. The GUI will remember your selected folders the next time you run it. It isn't pretty, but it seems to work nice. I offer it up here for your testing pleasure. I hope it will be useful to someone else.

Synchronize Data to Computer
One of the things that is interesting about CAR is it's utter lack of connectivity. Obviously, I have some internet access, or I wouldn't be sending this message your way, but there is certainly very little you can do with a phone. Even if you could, we are fielding 22 Androids and while we will be collecting a lot of data, we don't need the added expense of equipping each one with a data enabled SIM chip. So, I needed a way to synchronize my data from the phones without using any kind of connectivity other than being in the same room with the laptop. I need to go from data collected on the phone to an aggregated CSV containing all the records. We use SPSS for analysis, so a CSV is perfect for import, and if you need to import more CSVs over time, it is easy to merge the data.

From the command line, run java -cp KoboPostProc.jar org.oyrm.kobo.postproc.ui.KoboPostProcFrame

This will give you a little GUI. It is titled "Kobo Post Processor". The GUI has five buttons.

Change XML source directory

Use to set the directory where you completed instances are stored. If you have an Android plugged in this will be \SDcard\odk\instances\

Change XML storage directory

Use to set the directory where you would like to copy all the completed forms. I do this so that you can store your completed forms off the phone in case you lose it or it gets stolen. Also, then you can delete them off the phone. App is smart enough not to duplicate what it has already copied previous forms.

Sync XML surveys

This is the button that actually backs up all your surveys from the phone to the hard disk, assuming you have set the directories using the previous 2 buttons. Click it and don't blink, there is a progress bar but this operation is very fast. Now, your forms are backed up to your machine. The storage directory is where the Transcriber will look when you tell it to make a CSV.

Change CSV storage Directory

Use this to set the directory where the CSV will be placed after the Transcriber does it's business with whatever forms it finds in the storage directory.

Transcribe XML to CSV

Just what is says on the tin. Click this and the app will look in the XML storage directory, it will read each completed form, it will scrape out the schema and write headers to the first line of the CSV, then it will write a record in the next line, then it will open each additional XML form and give each one a row in the CSV. The CSV will be stored in the directory you chose for storage. XML files which had been previously written to CSV will not be rewritten to the CSV and so there is no need to sanitize your XML storage directory between runs of the transcriber. If the Transcriber comes across an XML form whose schema is different than the others, it is able to handle that by modifying the schema to include new fields and inserting blank fields for that column in all the previous records. While these headers are updated to accommodate changes to the schema the existing data will not be altered or deleted by the transcription process.

Random Record Generator
I also needed a way to test my system with lots and lots of records. My survey has more than 300 questions, so it is unwieldy and making fake records takes forever. So, to be able to test the system under the weight of hundreds of records, we included a randomizer. You point it at an completed ODK form and tell it to make random records, how many and where to put them, and it churns them out. In a second it will make you 100 fake records. 100 is the default, but you can do more. The data is random strings, longs, ints, & date stamps which are generated by inspecting the completed ODK form to determine the data type for each question.

From the command line, run java -cp KoboPostProc.jar org.oyrm.kobo.postproc.test.KoboXMLGen <XMLFile> <DestinationDirectory>

XMLFile should refer to an instance, a completed form as is stored in the ODK/Instances/ directory after a survey is completed.

DestinationDirectory should refer to any nice empty folder. It doesn't have to be empty, but this will make 100 files, so it's a good idea to put them someplace.

Example: java -cp KoboPostProc.jar org.oyrm.kobo.postproc.test.KoboXMLGen ../Instance/CAR_2009-09-27_17-41-53.xml ../test/
