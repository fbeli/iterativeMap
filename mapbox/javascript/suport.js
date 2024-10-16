function save_cookies(cookie_name, cookie_value){
    document.cookie = cookie_name+"="+cookie_value;
}

let latitude;
let longitude;
let user_id;

//Received_ vieram de um link direto de ponto ou rota
let received_pointId;
let received_lat;
let received_long;
let received_zoom;
let received_routeId;
let entrada = window.location.href;

let point_link;

async function read_cookies(){

    let token = "token=";
    let name ="name=";
    let lat = "latitude=";
    let long = "longitude=";
    let userId = "user_id=";
    let access = "access=";
    let decodedCookie = decodeURIComponent(document.cookie);
    let ca = decodedCookie.split(';');
    let c;
    for(let i = 0; i <ca.length; i++) {
        c = ca[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) == 0) {
            userName = c.substring(name.length, c.length);
            get_li_after_login(userName);
        }
        if (c.indexOf(token) == 0) {
            accessToken = c.substring(token.length, c.length);
            //testToken();
        }
        if (c.indexOf(lat) == 0) {
            latitude = c.substring(lat.length, c.length);
        }
        if (c.indexOf(long) == 0) {
           longitude = c.substring(long.length, c.length);
        }
        if(c.indexOf(userId) == 0){
            user_id = c.substring(userId.length, c.length);
        }
        if (c.indexOf(access) == 0) {
            firstTime = false;
        }else{
            save_cookies(access, 1);
        }
    }
    if(longitude == 'undefined' || latitude == 'undefined'){
        longitude =    getLongitude();
        latitude =   getLatitude();
        save_cookies('latitude', latitude);
        save_cookies('longitude', longitude);
    }
}


async function upload_point_photo(point_id, fileInput){

    let formData = new FormData();

    formData.append('files', fileInput.files[0]);

    if(fileInput.files[0] == undefined){
        return;
    }
    var myHeaders = new Headers();

    myHeaders.append('Authorization', accessToken);

    var requestOptions = {
        method: 'PUT',
        headers: myHeaders,
        body: formData,
        redirect: 'follow'
    };

    await fetch(config.cadastro_url+"/"+point_id, requestOptions)
        .then(response => response.text())
        .then(result => console.log(result))
        .catch(error => console.log('error', error));
}

async function translate_point(point_id) {

    let language = document.getElementById("lang_"+point_id).value;
    let url = config.translate_point + point_id + "&language=" + language;
    fetch(url, {
        method: "PUT",
        headers: {
            "Authorization": accessToken,
            "Content-Type": "application/json"
        }
    }) .then(response => response.json())
        .then(data => {
            if(data.status == "200"){
                alert("Point translated");
            }else{
                alert(data.error);
            }
        })

        .catch(error => {
            console.log(error);
           alert(error);

        });
}

function is_logged() {
    if (accessToken !== undefined && accessToken !== null && accessToken !== "") {
        configPersonalInfo();
        return true;
    } else {
        return false;
    }
}

function criarOpcoesSelect(dados) {

    const selectElementRoute = document.getElementById("select_route_to_add");
    let selectRouteOptions = "";
    dados.forEach(item => {
        selectRouteOptions += `<option value="${item.roteiroId}">${item.title}</option>`;
    });
    selectElementRoute.innerHTML = selectRouteOptions;
}

async function readStartPageVariables(){

    console.log(entrada);
    const parts = entrada.split("#")[1].split("/");

    received_zoom = parseFloat(parts[0]);
    received_lat = parseFloat(parts[1]);
    received_long = parseFloat(parts[2]);
    const pointIdParam = parts.find(param => param.startsWith('pointId='));
    if (pointIdParam) {
        received_pointId = pointIdParam.split('=')[1];
        if(received_pointId != undefined){
            getPoint(received_pointId).then ( feature => {
                console.info("Feature: "+feature);
                show_infos(feature);
            });
            return true;
        }
    }else{
        return false;
    }
}

class Feature {
    constructor(audio, description, photo, pointId, shortDescription, title, user_guide, user_id, user_instagram, user_name, user_share, long, lat) {
        this.properties = new Propeties(audio, description, photo, pointId, shortDescription, title, user_guide, user_id, user_instagram, user_name, user_share);
        this.geometry = new Geometry(long, lat);
    }

}
class Geometry{
    constructor(long, lat) {
        this.coordenates = [long,lat];
    }
}
class Propeties{
    constructor(audio, description, photo, pointId, shortDescription, title, user_guide, user_id, user_instagram, user_name, user_share) {
        this.audio = audio;
        this.description = description;
        this.photo = photo;
        this.pointId = pointId;
        this.shortDescription = shortDescription;
        this.title = title;
        this.user_guide = user_guide;
        this.user_id = user_id;
        this.user_instagram = user_instagram;
        this.user_name = user_name;
        this.user_share = user_share;
    }
}

