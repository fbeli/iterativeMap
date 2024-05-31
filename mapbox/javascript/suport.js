function save_cookies(cookie_name, cookie_value){
    document.cookie = cookie_name+"="+cookie_value;
}

let latitude;
let longitude;
let user_id;

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
            //get_li_after_login(userName);
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
    if(longitude == 'undefined' || latitude == ''){
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
   
    let data = {
        title: title,
        language: language,
        description: description

    };
    fetch(config.update_point + point_id, {
        method: "PUT",
        headers: {
            "Authorization": accessToken,
            "Content-Type": "application/json"
        },
        body: JSON.stringify(data)
    })



}