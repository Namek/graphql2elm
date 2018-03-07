module Main exposing (..)

import Html exposing (..)
import Html.Attributes as Attr exposing (style, type_, value)
import Html.Events exposing (onClick, onInput)
import Misc exposing (..)
import Ports exposing (elmCodeGenerationResult, generateElmCode)


main : Program Never Model Msg
main =
    Html.program
        { init = init
        , view = view
        , update = update
        , subscriptions = subscriptions
        }



-- SUBSCRIPTIONS


subscriptions : Model -> Sub Msg
subscriptions model =
    elmCodeGenerationResult ElmCodeGenerationResult



-- MODEL


type alias Model =
    { representNullableInEmittedGraphQLComment : Bool
    , emitMaybeForNullables : Bool
    , typePrefix : String
    , schema : String
    , query : String
    , elmCode : Maybe String
    }


init : ( Model, Cmd Msg )
init =
    let
        model =
            Model True True "Q" "" "" Nothing
    in
    ( model, Cmd.none )



-- UPDATE


type Msg
    = ToggleNullableInComment
    | ToggleEmitMaybeForNullableFields
    | ChangeTypePrefix String
    | SetSchema String
    | SetQuery String
    | GenerateElmCode
    | ElmCodeGenerationResult (Maybe String)


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        ToggleNullableInComment ->
            { model | representNullableInEmittedGraphQLComment = not model.representNullableInEmittedGraphQLComment } => Cmd.none

        ToggleEmitMaybeForNullableFields ->
            { model | emitMaybeForNullables = not model.emitMaybeForNullables } => Cmd.none

        ChangeTypePrefix prefix ->
            { model | typePrefix = prefix } => Cmd.none

        SetSchema schema ->
            { model | schema = schema } => Cmd.none

        SetQuery query ->
            { model | query = query } => Cmd.none

        GenerateElmCode ->
            model => generateElmCode ( model.query, model.schema )

        ElmCodeGenerationResult res ->
            { model | elmCode = res }
                => Cmd.none



-- VIEW


view : Model -> Html Msg
view model =
    div [ style [ "padding-left" => "20px" ] ]
        [ div []
            [ viewTextarea "Schema" SetSchema
            , viewTextarea "Query" SetQuery
            ]
        , div []
            [ checkbox
                ToggleNullableInComment
                "Represent nullable in comment with generated GraphQL query"
                model.representNullableInEmittedGraphQLComment
            ]
        , div []
            [ checkbox
                ToggleEmitMaybeForNullableFields
                "Emit Maybe for nullable fields"
                model.emitMaybeForNullables
            ]
        , label []
            [ text "Generated type prefix:"
            , input
                [ type_ "text"
                , onInput ChangeTypePrefix
                , value model.typePrefix
                , style [ "width" => "30px", "margin-left" => "10px" ]
                ]
                []
            ]
        , div [] [ button [ onClick GenerateElmCode ] [ text "\x1F937 Generate Elm" ] ]
        , viewIf (model.elmCode /= Nothing)
            (div []
                [ h1 [] [ text "Result" ]
                , pre [] [ text <| Maybe.withDefault "" model.elmCode ]
                ]
            )
        ]


checkbox : msg -> String -> Bool -> Html msg
checkbox msg name isChecked =
    label []
        [ input [ type_ "checkbox", onClick msg, Attr.checked isChecked ] []
        , text name
        ]


viewTextarea : String -> (String -> msg) -> Html msg
viewTextarea headerText msg =
    div [ style [ "display" => "inline-block" ] ]
        [ h1 [] [ text headerText ]
        , textarea [ onInput msg, Attr.cols 50, Attr.rows 20 ] []
        ]
