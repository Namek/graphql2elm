# GraphQL to Elm
Generate [Elm](http://elm-lang.org) code based on [GraphQL](http://graphql.org/) query and schema. Generated code uses [jamesmacaulay/elm-graphql](https://github.com/jamesmacaulay/elm-graphql) library.

See [online web generator](https://namek.github.io/graphql2elm/).

# Status

Feature list consists of:

- generate record aliases (`type alias`) and enums which are used in given query
- return type of whole query is being shortened to the first field that consists of more than one subfields
- config: known types - so it won't generate new ones but import the existing ones
- config: map backend types to front-end types - like `Boolean` -> `Bool` between [Elixir](https://elixir-lang.org/) and Elm
- config: decoders for known types
- config: support nullable fields by using [Maybe](http://package.elm-lang.org/packages/elm-lang/core/latest/Maybe)

This stuff is available on [web generator](https://github.com/Namek/graphql2elm/tree/master/graphql2elm-js).

I have no plans for further development since I found another tool with a more interesting approach. It generates code for a whole GraphQL API + generates a decent API for writing queries. It's https://github.com/dillonkearns/elm-graphql - check it out.

## Roadmap (deprecated)

- field arguments (basics done)
- input variables (basics done)
- fragments (?)
- extend configuration of [online web generator](https://namek.github.io/graphql2elm/) for known types

# Dev experience

- Whole parsing + generation logic is implemented with [Kotlin](https://kotlinlang.org/). Because it's nice. And it transpiles to JavaScript.
- Parsing of GraphQL queries is done with custom parser. Previously, I started with ANTLR but decided to try and write parser by hand instead of generating it and I'm actually pretty satisfied with it.
- Schema is a JSON which comes from a result of GraphQL introspection query (`__schema`). There's also a parser for it (well, it should be called Interpreter because it's just a JSON).
- The [web generator](https://github.com/Namek/graphql2elm/tree/master/graphql2elm-js) is done with Elm 0.19 bootstrapped by code written in Kotlin, later transpiled to JS.


## Build

Build web generator by calling `gradle assembleWeb` from the root directory. You'll find the result in `build/web`.

