CC=javac

WrapTmpl.class:				WrapTmpl.java
			$(CC) WrapTmpl.java

clean:
			rm -f WrapTmpl*.class
