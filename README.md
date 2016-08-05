# Java class `EnvReplacer`

Drop in solution to replace environment variables in either files or `List<String>` objects.

## Java Usage

Copy the class and put it in a package which suits you. The class consists of two static methods which you may use. In short, the class works like this: It replaces occurrences of `${ENV_VAR}` with the content of the environment variable.

It doesn't do this very nicely or elaborately, or fast. It's a stupid class, but it works. Perhaps don't use it for large files. I wrote it to be able to template Dockerfiles inside a [GoCD Plugin](http://www.go.cd). If you just need something `bash`y, do it with `perl` or even `envsubst`.

### Methods

#### `List<String> EnvReplacer.replaceEnv(List<String>)`

Replaces environment variables in a `List` of `String`s. As a second step, it will unescape any backslashed characters in the string. If you have a substring like `\${HOME}`, this will be rendered to `${HOME}`, while `${HOME}` will be replaced with the content of the `HOME` environment variable.

Returns a new `List<String>` containing the replaced strings.

#### `EnvReplacer.replaceEnv(Path inFile, Path outFile)`

Works exactly like the above method, but reads from a file and writes to a file. May throw an `IOException`, e.g. if the `inFile` cannot be found, or if you do not have write access to `outFile`.

## Command Line Usage

In case you just compile the class (`javac EnvReplacer.java`), you may also use the class as a command line tool.

```
$ java EnvReplacer <inFile> [<outFile>]
```

It takes an `<inFile>` as a mandatory parameter; if `<outFile>` is given, the output is written to that file path; otherwise the output is written to `stdout`.

## License

Copyright 2016 Haufe-Lexware GmbH & Co. KG.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
