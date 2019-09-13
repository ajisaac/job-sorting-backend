### Simple job sorting backend
A simple application to assist with my job search.

Meant to be run locally alongside the [job-sorting-frontend](https://github.com/ajisaac/job-sorting-frontned).

Just pull it down, build it<br>
`mvn package`<br>

Run it like<br>
`java -jar target/job-backend-1.0.0.jar server local.yml`

Should change some of the hardcoded urls particularly in `Database.java`<br>
where you'll also want to set up your database and alter the class as<br>
is necessary. 

The `MARIADB_CREDS` is just an environment variable local to your machine.<br>

There's one string in `JobService.java` that should be updated to a<br>
directory where you'll store the input files.

***
##### Input Files
The input is directory, in that directory are a bunch of `.json` files.<br>
You need to provide your own data. This application doesn't gather input<br>
besides what you supply to it through your input directory.<br>
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
removing duplicates, using the url as a key.


The schema contains a single table that looks like
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
