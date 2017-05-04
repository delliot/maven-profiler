#Maven Profiler

A time execution recorder for Maven which log time taken by each mojo in your build lifecycle.

##Installation

`$M2_HOME` refers to maven installation folder.

```
.
├── bin
├── boot
├── conf
└── lib
```


### Maven >= 3.3.x

Place maven-profiler.jar in $M2_HOME/lib/ext
*or*

Use the new [core extensions configuration mechanism](http://takari.io/2015/03/19/core-extensions.html) by creating a `${maven.multiModuleProjectDirectory}/.mvn/extensions.xml` file with:

	<?xml version="1.0" encoding="UTF-8"?>
	<extensions>
	    <extension>
	      <groupId>fr.jcgay.maven</groupId>
	      <artifactId>maven-profiler</artifactId>
	      <version>2.5</version>
	    </extension>
	</extensions>


##Usage

Runs by default once extension in installed. 

This also works when `mvn` is executed on multiple threads (option `-T`).


### JSON

	mvn install -Dprofile -DprofileFormat=JSON

```
{
  "name": "maven-profiler",
  "time": "44681 ms",
  "goals": "clean install",
  "date": "2017/01/21 19:10:04",
  "parameters": "{profile=true, profileFormat=JSON}",
  "projects": [
    {
      "project": "maven-profiler",
      "time": "43378 ms"
    }
  ]
}
```

## Build status



## Release

    mvn -B release:prepare release:perform
