# GraphQL to Elm
Generate [Elm](http://elm-lang.org) code based on [GraphQL](http://graphql.org/) query and schema. The code uses [jamesmacaulay/elm-graphql](https://github.com/jamesmacaulay/elm-graphql) library.

# Status

Sample queries and schema are hardcoded but feature list consists of:

- generates record aliases (`type alias`) and enums which are used in given query  
- return type of whole query is being shortened to the first field that consists of more than one subfields
- config: known types - so it won't generate new ones but import the existing ones
- config: map backend types to front-end types - like `Boolean` -> `Elm` between [Elixir](https://elixir-lang.org/) and Elm
- config: decoders for known types
- config: support nullable fields by using [Maybe](http://package.elm-lang.org/packages/elm-lang/core/latest/Maybe)


## Roadmap

- field arguments, input variables
- fragments - although [jamesmacaulay/elm-graphql](https://github.com/jamesmacaulay/elm-graphql) doesn't seem to support them
- web site with configuration and front-end generator (ANTLR4 runtime is Java lib so it looks like a blocker - TeaVM won't do for now due to [issue #331](https://github.com/konsoletyper/teavm/issues/331))


## Discussion

The complete idea of this tool is yet undecided because there are 2 paths:
1. a tool which is automatically launched within development pipeline which checks on query files and schema, then regenerates whole Elm modules (`.elm` files with code)
2. a tool that generates something on web and doesn't have to produce compilable code, just as close as useful to final, for custom modification

The path #1 feels like superior/superset to #2 but it may need more configuration and support more specific cases (?).
However focusing on the #2 would produce a more friendly tool, approachable even by beginners.

Thus, as of I'm writing this, I'm going to balance between the two. 

# Dev

- Whole thing is implemented with [Kotlin](https://kotlinlang.org/) lang. Because it's nice. And I hoped for transpilation to JavaScript.
- Parsing of GraphQL queries is done with [ANTLR4](https://github.com/antlr/antlr4).
- Schema is a JSON which comes from a result of GraphQL introspection query (`__schema`)
