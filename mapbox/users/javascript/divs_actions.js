let zoom = 16;


function welcome_div_show() {
    show_div("welcome_div_id");
}

function error_div_event(used_div, erro_message) {
    document.getElementById("erro_alert_text").innerHTML = erro_message;
    if (document.getElementById("erro_alert_div").style.display == 'block') {
        document.getElementById("erro_alert_div").style.display = 'none';
        if (previous_div != null) {
            show_div(previous_div);
        }
    } else {
        fechar_divs()
        document.getElementById("erro_alert_div").style.display = 'block';
        previous_div = used_div;
    }
}

function show_create_points_warning() {
    document.getElementById("create_points_warning").style.display = 'flex';
}

function option_create_points() {
    fechar_divs();
    if(accessToken !== undefined && accessToken!== null && accessToken !== "") {
    if (create_point) {
        create_point = false;
        document.getElementById("a_create_points").innerHTML = 'AddSpot';
    } else {
        create_point = true;
        show_create_points_warning();
        document.getElementById("a_create_points").innerHTML = 'StopAdd';

    }
    }else{
        document.getElementById("div_please_login").style.display = 'flex';
    }
}

function login_opt_ul_show() {
    fechar_divs();
    if (document.getElementById("login_opt_ul").style.display == 'none') {
        document.getElementById("login_opt_ul").style.display = 'flex';
    } else {
        document.getElementById("login_opt_ul").style.display = 'none';
    }
}

function close_error_div() {
    error_div_event(previous_div);
}

function fechar_divs() {
    document.getElementById("starting").style.display = 'none';

}

function cadastro() {
    show_div("cadastro_div");
}

function login() {
    show_div("login_div");
}

function sign_up() {
    show_div("sign_up_div");
}

function forgetPassword() {
    show_div("forget_password_div");
}
function show_div(el) {
    fechar_divs();
    document.getElementById(el).style.display = 'block';
}

function introduce() {

    fechar_divs();
    let mapDescription;
    if (navigator.language.indexOf("pt") > -1) {
        mapDescription = `
        Descubra e Contribua para Nosso Mapa Turístico feito pela comunidade!<br><br>
        Procurando explorar os melhores pontos turísticos em sua área? Nosso mapa interativo tem tudo o que você precisa!<br><br>

Por que escolher nosso mapa?<br>

1. Cobertura Abrangente: Dos pontos turísticos famosos às joias escondidas, nosso mapa inclui tudo.<br>
2. Fácil de Usar: Amplie locais, obtenha informações detalhadas e rotas em poucos cliques.<br>
3. Colaboração Comunitária: Compartilhe suas próprias descobertas e ajude outros a encontrar lugares incríveis.<br>
4. Recomendações Personalizadas: Filtragem e busca com base em suas preferências.<br>
5. Compartilhamento Social: Compartilhe seus pontos turísticos favoritos com amigos e família.<br>
Junte-se à nossa comunidade de aventureiros hoje, contribua com o mapa e descubra tesouros escondidos juntos!<br>
`;
    } else {

        mapDescription = `
Discover and Contribute to Our Community Touristic Map!<br><br>
Looking to explore the best touristic spots in your area? Our interactive map has you covered!<br><br>
Why choose our map?<br>
1. Extensive Coverage: From famous landmarks to hidden gems, our map includes it all.<br>
2. Easy to Use: Zoom in on locations, get detailed info, and directions in a few clicks.<br>
3. Community Collaboration: Share your own discoveries and help others find amazing spots.<br>
4. Personalized Recommendations: Filter and search based on your preferences.<br>
5. Social Sharing: Share your favorite touristic points with friends and family.<br><br>
Join our community of adventurers today, contribute to the map, and uncover hidden treasures together!
`;
    }

    document.getElementById("info_info").innerHTML = mapDescription;
    document.getElementById("info_title").innerHTML = "Introducing Ourselves";
    document.getElementById("info_highlight").innerHTML = "";
    document.getElementById("info_div").style.display = 'flex';

}

function add_logo(){
    let div_left = document.getElementsByClassName("mapboxgl-ctrl-bottom-left");
    div_left[0].innerHTML = "<a class=\"mapboxgl-ctrl-logo\" target=\"_blank\" rel=\"noopener nofollow\" href=\"https://www.mapbox.com/\" aria-label=\"Mapbox logo\"></a>" +
        "<a class=\"mapboxgl-ctrl-logo\"  href=\"https://www.guidemapper.com/img/name_logo.png\" aria-label=\"GuideMapper\"><img class=\"img_logo\" src=\"https://guidemapper.com/img/name_logo.png\"></a>";

    let div_top_right = document.getElementsByClassName("mapboxgl-ctrl-top-right");
    div_top_right[0].innerHTML = "<div class=\"zoom\" ><div class='inside_zoom' id='inside_zoom'>"+zoom+" </div></div>";
    div_top_right[0].class = "mapboxgl-ctrl-top-right zoom";
}

