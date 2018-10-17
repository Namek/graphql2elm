# GraphQL to Elm
Generate [Elm](http://elm-lang.org) code based on [GraphQL](http://graphql.org/) query and schema. Generated code uses [jamesmacaulay/elm-graphql](https://github.com/jamesmacaulay/elm-graphql) library.

See [online web generator](https://namek.github.io/graphql2elm/).

# Status

Feature list consists of:

- generates record aliases (`type alias`) and enums which are used in given query  
- return type of whole query is being shortened to the first field that consists of more than one subfields
- config: known types - so it won't generate new ones but import the existing ones
- config: map backend types to front-end types - like `Boolean` -> `Bool` between [Elixir](https://elixir-lang.org/) and Elm
- config: decoders for known types
- config: support nullable fields by using [Maybe](http://package.elm-lang.org/packages/elm-lang/core/latest/Maybe)

Basic stuff is already available on [web generator](https://github.com/Namek/graphql2elm/tree/master/graphql2elm-js).

## Roadmap

- field arguments (somewhat done)
- input variables (WIP)
- fragments (?)
- extend configuration of [online web generator](https://namek.github.io/graphql2elm/) for known types

# Dev

- Whole logic thing is implemented with [Kotlin](https://kotlinlang.org/) lang. Because it's nice. And it transpiles to JavaScript.
- Parsing of GraphQL queries is done with custom parser
- Schema is a JSON which comes from a result of GraphQL introspection query (`__schema`)
- [web generator](https://github.com/Namek/graphql2elm/tree/master/graphql2elm-js) is done with Elm


## Build

Build web generator by calling `gradle assembleWeb`. You'll find it in `build/web`.

