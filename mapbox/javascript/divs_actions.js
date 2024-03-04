function welcome_div_show(){
    show_div("welcome_div_id");
}

function error_div_event(used_div, erro_message){
    document.getElementById("erro_alert_text").innerHTML = erro_message;
    if(document.getElementById("erro_alert_div").style.display == 'block'){
        document.getElementById("erro_alert_div").style.display = 'none';
        if(previous_div != null){
            show_div(previous_div);
        }
    }else {
        fechar_divs()
        document.getElementById("erro_alert_div").style.display = 'block';
        previous_div = used_div;
    }
}

function show_create_points_warning(){
    document.getElementById("create_points_warning").style.display = 'flex';
}

function option_create_points(){
    fechar_divs();
    if(create_point){
        create_point = false;
        document.getElementById("a_create_points").innerHTML = 'Create Points';
    } else {
        create_point = true;
        show_create_points_warning();
        document.getElementById("a_create_points").innerHTML = 'Do not create new points';

    }
}

function  login_opt_ul_show(){
    fechar_divs();
    if(document.getElementById("login_opt_ul").style.display == 'none'){
        document.getElementById("login_opt_ul").style.display = 'flex';
    }else {
        document.getElementById("login_opt_ul").style.display = 'none';
    }
}
function close_error_div(){
    error_div_event(previous_div);
}

function fechar_divs(){
    document.getElementById("login_div").style.display = 'none';
    document.getElementById("sign_up_div").style.display = 'none';
    document.getElementById("cadastro_div").style.display = 'none';
    document.getElementById("welcome_div_id").style.display = 'none';
    document.getElementById("starting").style.display = 'none';
    document.getElementById("create_points_warning").style.display = 'none';
    document.getElementById("login_opt_ul").style.display = 'none';
    document.getElementById("info_div").style.display = 'none';
    document.getElementById("cadastro_audio").value = null;

}
function cadastro(){
    show_div("cadastro_div");
}
function login(){
    show_div("login_div");
}
function sign_up(){
    show_div("sign_up_div");
}

function show_div(el){
    fechar_divs();
    document.getElementById(el).style.display = 'block';
}