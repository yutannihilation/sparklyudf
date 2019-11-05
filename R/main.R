#' @import sparklyr
#' @export
sparklyudf_register <- function(sc, fun) {
  sparklyr::invoke_static(sc, "sparklyudf.Main", "register_hello", spark_session(sc), forge::cast_string(fun))
}
