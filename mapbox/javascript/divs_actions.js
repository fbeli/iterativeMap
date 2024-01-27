function welcome_div_show(){
    show_div("welcome_div_id");

}

function fechar_divs(){
    document.getElementById("login_div").style.display = 'none';
    document.getElementById("sign_up_div").style.display = 'none';
    document.getElementById("cadastro_div").style.display = 'none';
    document.getElementById("welcome_div_id").style.display = 'none';

    document.getElementById("starting").style.display = 'none';

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