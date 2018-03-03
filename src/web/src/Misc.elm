module Misc exposing (..)

import Html exposing (Html, text)


(=>) : a -> b -> ( a, b )
(=>) =
    (,)


{-| infixl 0 means the (=>) operator has the same precedence as (<|) and (|>),
meaning you can use it at the end of a pipeline and have the precedence work out.
-}
infixl 0 =>


viewIf : Bool -> Html msg -> Html msg
viewIf cond el =
    if cond then
        el
    else
        text ""


either : a -> a -> Bool -> a
either a1 a2 cond =
    if cond then
        a1
    else
        a2
