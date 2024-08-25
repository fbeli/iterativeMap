let arrayRouteTitles = [];
let arrayRouteIds = [];
function getMyRoutes(){

    let url = config.env + config.get_routes_endpoint + "?instagram=" + my_instagram + "&language=" + language;
    fetch(url, {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
            "Authorization": accessToken
        }
    })
        .then(response => response.json())
        .then(data => {
            if(data.roteiros.length > 0) {
                criarOpcoesSelect(data.roteiros);
            }
        })
}

async function addToRoute(){
    let routeId = document.getElementById("select_route_to_add").value;

    let data = {
        pointId: pointId,
        roteiroId: routeId
    };
    fetch(config.env + config.get_routes_endpoint, {
        method: "PUT",
        headers: {
            "Authorization": accessToken,
            "Content-Type": "application/json"
        },
        body: JSON.stringify(data)
    })
        .then(response => response.json())
        .then(data => {
            if (data.status == "200") {
                fechar_divs()
                document.getElementById("info_info").innerHTML = "You can review this route updated soon";
                document.getElementById("info_title").innerHTML = "Route updated";
                document.getElementById("info_highlight").innerHTML = "";
                document.getElementById("info_div").style.display = 'flex';
            } else {
                alert(data.error);
            }
        })
        .catch(error => {
            console.log(error);
            alert(error);
        });
}

async function addRoute(){
    let place = document.getElementById("add_route_place").value;
    let title = document.getElementById("add_route_title").value;
    let description = document.getElementById("add_route_description").value;
    let language = document.getElementById("loc_language").value

    let data = {
        pointId: pointId,
        title: title,
        description: description,
        place: place,
        language: language
    };
    fetch(config.env + config.get_routes_endpoint, {
        method: "POST",
        headers: {
            "Authorization": accessToken,
            "Content-Type": "application/json"
        },
        body: JSON.stringify(data)
    })
        .then(response => response.json())
        .then(data => {
            if (data.status == "200") {
                fechar_divs()
                document.getElementById("info_info").innerHTML = "You can review this route updated soon";
                document.getElementById("info_title").innerHTML = "Route updated";
                document.getElementById("info_highlight").innerHTML = "";
                document.getElementById("info_div").style.display = 'flex';
            } else {
                alert(data.error);
            }
        })
        .catch(error => {
            console.log(error);
            alert(error);
        });
}