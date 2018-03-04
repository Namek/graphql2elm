port module Ports exposing (..)


port generateElmCode : ( String, String ) -> Cmd msg


port elmCodeGenerationResult : (Maybe String -> msg) -> Sub msg
