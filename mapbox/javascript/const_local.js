const config = {
    env: "http://localhost:8081",
    login_url: "http://localhost:8081/login",
    sign_in_url: "http://localhost:8081/signin",
    cadastro_url: "http://localhost:8081/point",
    gerar_geo_file_url:"http://localhost:8081/gerarArquivoParaMapa",
    gerar_arquivo_to_aprove_url: "http://localhost:8081/gerarArquivoAprovacao",
    end_point_url: "https://webservice.guidemapper.com:8081",
    get_user: "http://localhost:8082/user/get_by_email/?email=",
    forget_password: "http://localhost:8081/user/forget",
    reset_url: "http://localhost:8081/user/reset_password",
    test_token: "http://localhost:8081/token",
    list_points_by_user: "http://localhost:8081/point/users?userId=",
    update_point: "http://localhost:8081/v2/point?pointId=",
    translate_point: "http://localhost:8081/v2/point/translate?pointId=",
    get_routes_endpoint: "/routes",
    point: "/point/"
}
