module Data.Settings exposing (Settings, decodeFromJson, decoder, encode)

import Json.Decode as Decode exposing (Decoder, succeed)
import Json.Decode.Pipeline as Pipeline exposing (optional, required)
import Json.Encode as Encode exposing (Value)


type alias Settings =
    { representNullableInEmittedGraphQLComment : Bool
    , emitMaybeForNullables : Bool
    , typePrefix : String
    , schema : String
    , query : String
    }


decoder : Decoder Settings
decoder =
    succeed Settings
        |> required "representNullableInEmittedGraphQLComment" Decode.bool
        |> required "emitMaybeForNullables" Decode.bool
        |> required "typePrefix" Decode.string
        |> required "schema" Decode.string
        |> required "query" Decode.string


encode : Settings -> Value
encode settings =
    Encode.object
        [ ( "representNullableInEmittedGraphQLComment"
          , Encode.bool settings.representNullableInEmittedGraphQLComment
          )
        , ( "emitMaybeForNullables", Encode.bool settings.emitMaybeForNullables )
        , ( "typePrefix", Encode.string settings.typePrefix )
        , ( "schema", Encode.string settings.schema )
        , ( "query", Encode.string settings.query )
        ]


decodeFromJson : Decode.Value -> Settings
decodeFromJson json =
    json
        |> Decode.decodeValue decoder
        |> Result.withDefault (Settings True True "Q" "" "")
