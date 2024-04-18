
function next(actual_div){

    next_div ="";
    switch (actual_div) {
        case "start_welcome":
            next_div="start_here_you_are";
            break;
        case "start_here_you_are":
            next_div="start_back_to_point";
            break;

        case "start_back_to_point":
            next_div="start_zoom";
            break;
        case "start_zoom":
            next_div="start_point";
            break;
        case "start_point":
            next_div="start_about_point";
            document.getElementById("img_back_start").src = "img/ponto2.png";
            break;
        case "start_about_point":
            next_div="start_description_point";
            document.getElementById("img_back_start").src = "img/ponto3.png";
            break;
        case "start_description_point":
            next_div="start_login";
            document.getElementById("img_back_start").src = "img/ponto1.png";
            break;
        case "start_login":
            next_div="start_add_point";
            break;
        case "start_add_point":
            document.getElementById("starting").style.display = "none";
            break;
    }

    document.getElementById(next_div).style.display = 'block';
    document.getElementById(actual_div).style.display = 'none';

}

async function testToken(){
    let data = {
        token: accessToken,
    };
    fetch(config.test_token, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(data)
    })
        .then(response => response.json())
        .then(data => {
            if(data.status == "200") {
                console.log(data);
            }
        })
        .catch(error => {
            document.getElementById("erro_alert_text").innerHTML = data.error;
            error_div_event("forget_password_div");
        });
}