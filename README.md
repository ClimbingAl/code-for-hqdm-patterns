# code-for-hqdm-patterns

This code can be used to build all the example data files and diagrams for the [HQDM documentation repo](https://github.com/ClimbingAl/HqdmPatterns).  The documentation is served here: https://climbingal.github.io/HqdmPatterns/.

Installation:

- Use Vscode and start in DevContainer
- Manually do the following:
  - `cd /home/vscode/`
  - `mkdir maven` & `mkdir dependencies`
  - In the maven directory:
    - `wget https://dlcdn.apache.org/maven/maven-3/3.9.5/binaries/apache-maven-3.9.5-bin.tar.gz`
    - `tar xvf apache-maven-3.9.5-bin.tar.gz`
    - `cd ~` open `.bashr` and add the following to the aliases section:
      `# Maven path settings
      export M2_HOME=/home/vscode/maven/apache-maven-3.9.5/bin
      export PATH=$PATH:$M2_HOME`
  - In the dependencies directory:
    - `git clone git@github.com:ClimbingAl/MagmaCore.git`
    - `cd MagmaCore`
    - `mvn install`

To run the java app(s):
  - You may need to set the Maven executable Path in Vscode to: /home/vscode/maven/apache-maven-3.9.5/bin/mvn
  - Run the `AircraftExampleApp.java`.  If successful the file(s) in the output-files directory should be re-created.
