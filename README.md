### Simple job sorting 
A simple application to assist with my job search.

##### Status
Work in major progress, at prototype level, so major changes happen frequently.

##### Running
Is standard Spring boot. <br>
Should run the javascript portion through babel. <br>
There's a few hardcoded strings, this isn't meant for public consumption yet.<br>
The `MARIADB_CREDS` is just an environment variable local to your machine.<br>
There's also a directory string, just grep for `/home/aaron`.

***
##### Input Files
Subject to change, just check the code at `JobsListFromDirectory.java` to see how it loads the data.<br>
There's a particular naming format for input files. //todo specify this
The input is directory, in that directory are a bunch of `.json` files.<br>
You need to provide your own data. <br>
For example, a file sitting in your input directory that looks something like:
```json
[
  {
    "title": "job title",
    "summary": "job description",
    "url": "url to the job application",
    "company": "the company related to the job listing",
    "location": "city where job is located",
    "postDate": "when the job was posted",
    "salary": "salary estimate if it exists"
  },
  {
    "title": "job title",
    "summary": "job description",
    "url": "url to the job application",
    "company": "the company related to the job listing",
    "location": "city where job is located",
    "postDate": "when the job was posted",
    "salary": "salary estimate if it exists"
  }
]
```

The app will parse these files when it loads, pop them into the database,<br>
removing duplicates, using a hashcode to determine uniqueness. //todo update


The schema contains a single table that looks like this, but changes a lot.
```mariadb
create table job
(
    id       int                            not null auto_increment,
    url      VARCHAR(2000)                  not null,
    summary  text          default "unknown" null,
    company  varchar(255)  default "unknown" null,
    location varchar(100)  default "unknown" null,
    postdate varchar(30)   default "unknown" null,
    salary   varchar(100)  default "unknown" null,
    jobstate varchar(10)   default "unknown" null,
    title    varchar(255)  default "unknown" null,
    primary key (id)
);
```

If you want to start fresh, just delete everything from your database,<br>
then restart the app, it will pull all the data back in. 

This app is meant to be run locally on your machine, not as a webapp.<br>
