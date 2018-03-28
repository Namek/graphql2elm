port module Ports exposing (..)

import Array exposing (Array)
import Json.Encode exposing (Value)


port generateElmCode : Value -> Cmd msg


port elmCodeGenerationResult : (String -> msg) -> Sub msg


port elmCodeGenerationError : (Array String -> msg) -> Sub msg
