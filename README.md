# Template Test
## Java implementation

This repository implements the template test in Java.
The same template test has already been implemented
in the C language in another repository, called
Template Test.  Read the README.md file in that repository
for more information about the template test.

The normal template test in this repository is
called Template.java.  Template.java uses the
Etaus.java random number generator to give
correct results in the chi square test.  This template
test is one of many random number tests for the
etaus random number generator.

## Template.java

Template.java uses an array of 1024 random integers,
either zero or one, as a template for matching
the equivalent output of the etaus random number generator.
The probability of not matching the first number in the
template is 1/2.
The probability of matching the first number in the
template is 1/4.
The probability of matching the first two numbers in
the template is 1/8, etc.
The probability of matching the entire array of
1024 integers is 1/(2^2048).
This program tests one million sample queues against
the template.
For each sample, the actual queue is popped and pushed,
before the queue is matched against the template.
After one million samples have been processed, a chi square
test is performed on the numbers of matches found
between the actual matches and the expected matches.

To test Template.java, run the tmpl.sh batch
procedure.  There are no parameters.

tmpl.sh

The output should be a valid chi square test.

## WrapTmpl.java

WrapTmpl.java is the same template test applied to a
shortened version of etaus.  The seed has been set,
so that the period length of etaus is 501024
generations.  Since the program takes 1 million samples,
the random number generator wraps around, and generates
an error message.

To test WrapTmpl.java, run the wrap.sh batch
procedure.  There are no parameters.

wrap.sh

The output should be a wrap-around error message.

## SinTempl.java

SinTempl.java is the same template test applied to a
modified etaus random number generator.  One million
samples are taken in this test, but the samples do not
follow a uniform distribution.  The purpose of this
test is to show how a non-uniform random number generator
fails the chi square test at the end of this program.

To test SinTempl.java, run the sintest.sh batch
procedure.  There are no parameters.

sintest.sh

The output should be a failed chi square test.

