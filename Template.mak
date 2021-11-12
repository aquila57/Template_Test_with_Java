CC=javac

Template.class:				Template.java
			$(CC) Template.java

clean:
			rm -f Template*.class
