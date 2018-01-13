# jqb2jooq
jQuery QueryBuilder meets JOOQ without the work.


What is jQuery QueryBuilder?

jqb is a super handy library for your website that allow for the creation of queries and filters.

http://querybuilder.js.org/

What is JOOQ?

To shamelessly steal their tagline "JOOQ: The easiest way to write SQL in Java". This sentiment I agree with completely. 
The JOOQ library provides a typesafe way to execute queries and is an absolute joy to use.

https://www.jooq.org/


This project aims to be the glue between these two awesome products. jqb2jooq provides the means of defining a mapping from
jQuery QueryBuilder filter fields to JOOQ auto-generated fields. Once you define this mapping jqb2jooq will handle the conversion
of QueryBuilder json to a JOOQ condition, ready to be included in your JOOQ query.
