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
    if (accessToken !== undefined && accessToken !== null && accessToken !== "") {
        if (create_point) {
            create_point = false;
            document.getElementById("a_create_points").innerHTML = 'Add Spot';
        } else {
            create_point = true;
            show_create_points_warning();
            document.getElementById("a_create_points").innerHTML = 'Stop Add';
            map.setZoom(17);
        }
    } else {
        document.getElementById("div_please_login").style.display = 'flex';
    }
}

function open_boomings() {
    fechar_divs();

    document.getElementById("booming_places").style.display = 'block';
}

function close_boomings(place) {

    fechar_divs();
    var la = latitude;
    var lo = longitude;
    if (place === "lisbon") {
        lo = "-9.13667";
        la = "38.70796";
    }
    if (place === "london") {
        la = "51.509903";
        lo = "-0.134511";
    }
    if (place === "madrid") {
        la = "40.41223";
        lo = "-3.70529";
    }
    if (place === "porto") {
        la = "41.143126";
        lo = "-8.61069";

    }
    if (place === "valencia") {
        la = "39.4789";
        lo = "-0.37635";

    }
    if (place === "rio_de_janeiro") {
        lo = "-43.19163";
        la = "-22.9318";
    }
    if (place === "barcelona") {
        la = "51.509903";
        lo = "-0.134511";
    }

    map.flyTo({
        center: [lo, la],
        zoom: defaultZoom
    });
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
    document.getElementById("login_div").style.display = 'none';
    document.getElementById("sign_up_div").style.display = 'none';
    document.getElementById("cadastro_div").style.display = 'none';
    document.getElementById("welcome_div_id").style.display = 'none';
    //document.getElementById("starting").style.display = 'none';
    document.getElementById("create_points_warning").style.display = 'none';
    //document.getElementById("login_opt_ul").style.display = 'none';
    document.getElementById("info_div").style.display = 'none';
    document.getElementById("cadastro_audio").value = null;
    document.getElementById("div_please_login").style.display = 'none';
    document.getElementById("forget_password_div").style.display = 'none';
    document.getElementById("sidebar_logout_div").style.display = 'none';
    document.getElementById("booming_places").style.display = 'none';
    if (document.getElementsByClassName("mapboxgl-popup").length > 0)
        document.getElementsByClassName("mapboxgl-popup").item(0).style.display = 'none';

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

function add_logo() {
    let div_left = document.getElementsByClassName("mapboxgl-ctrl-bottom-left");
    div_left[0].innerHTML = "<a class=\"mapboxgl-ctrl-logo\" target=\"_blank\" rel=\"noopener nofollow\" href=\"https://www.mapbox.com/\" aria-label=\"Mapbox logo\"></a>" +
        "<a class=\"mapboxgl-ctrl-logo\"  href=\"https://www.guidemapper.com/img/name_logo.png\" aria-label=\"GuideMapper\"><img class=\"img_logo\" src=\"https://guidemapper.com/img/name_logo.png\"></a>";

    let div_top_right = document.getElementsByClassName("mapboxgl-ctrl-top-right");
    div_top_right[0].innerHTML = "<div class=\"zoom\" ><div class='inside_zoom' id='inside_zoom'>" + zoom + " </div></div>";
    div_top_right[0].class = "mapboxgl-ctrl-top-right zoom";
}


//const element = document.getElementsByClassName("mapboxgl-ctrl-geolocate");
//element.addEventListener("click", myFunction);
function myFunction() {
    save_cookies("latitude", map.getCenter().lat);
    save_cookies("longitude", map.getCenter.lng);
}

function show_infos(feature) {

    document.getElementById("desc_title").innerHTML = feature.properties.title;
    document.getElementById("point_info").style.display = "block";

    coords = feature.geometry.coordinates;

    //test audio
    if (feature.properties.audio !== undefined && feature.properties.audio.length > 2) {
        audio_link = feature.properties.audio;
        document.getElementById("point_info_audio").style.display = "block";
        if (navigator.userAgent.indexOf("iPhone") > -1) {
            document.getElementById("point_info_audio_apple").style.display = "block";
            //document.getElementById("point_info_audio_apple_bt").onclick = "play_on_safari(" + feature.properties.audio + ")";
        } else {
            document.getElementById("point_info_audio_all").style.display = "block";
            document.getElementById("point_info_audio_all").innerHTML = "<audio className='audio' controls> <source src=" + feature.properties.audio + " type='audio/mpeg'/> Your browser does not support the audio element.</audio>"
            //document.getElementById("point_info_audio_all_source").src = feature.properties.audio;
        }
    }
    //use_info
    document.getElementById("point_info_user_info").innerHTML = "Created by: " + feature.properties.user_name;
    if (feature.properties.user_guide === 'true') {
        document.getElementById("point_info_sh_professional").innerHTML = "is professional guide, <a href='#' onclick='getUser( \' " + feature.properties.user_id + "\')'> contact.</a></p><br/>";
    }
    if (feature.properties.user_share === 'true') {
        document.getElementById("point_info_sh_share").innerHTML = feature.properties.user_instagram;
    }

    if (feature.properties.photo !== undefined && feature.properties.photo.length > 2) {
        document.getElementById("point_info_img").style.display = "block";
        document.getElementById("point_info_img").innerHTML = "<img style='width: 100%' src=" + feature.properties.photo + " alt=" + feature.properties.title + "/> <br/>";

    }
    document.getElementById("point_info_sh_description").innerHTML = feature.properties.description;
    configScreen();
}

let altura_tela;

function configScreen() {
    altura_tela = window.screen.height;
    largura_tela = window.screen.width;
    mapa_height = 0.5;
    altura_div_info = altura_tela - (altura_tela * (1 - mapa_height));
    mapa_height = 100 * mapa_height;
    alterar_altura_mapa(mapa_height + "%");
    document.getElementById("point_info").style.position = "relative";
    document.getElementById("point_info").style.top = altura_div_info + "px";
    document.getElementById("point_info_media").style.width = largura_tela;
}

function alterar_altura_mapa(altura) {
    document.getElementById("map").style.height = altura;
}

function close_info() {

    document.getElementById("point_info").style.display = "none";
    document.getElementById("point_info_audio").style.display = "none";
    document.getElementById("point_info_audio_apple").style.display = "none";
    audio_link = "";
    document.getElementById("point_info_audio_all").style.display = "none";
    document.getElementById("point_info_img").style.display = "none";

    alterar_altura_mapa("100%");

}