let arrayRouteTitles = [];
let arrayRouteIds = [];
let  routesMap = new Map();
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

let search_value="";
async function search_route(page) {
    document.getElementById("route_list").innerHTML = "";
    let url = config.env + config.get_routes_endpoint;// + "/route/search?longitude="+longitude+"&latitude="+latitude+"&distance=1000";
    if (page < 1) {
        search_value = document.getElementById("search_value").value;
    }
    if (search_value.length > 2) {
        url = url + "?title=" + search_value;
    }else {
        error_div_event("booming_places","At least 3 characters");
        return;

    }
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
                let arrayPoints = [];
                div_rota = document.getElementById("route_list");
                for(let i = 0; i < data.roteiros.length; i++) {
                    div_detail_name = "detail_"+data.roteiros[i].roteiroId;
                    points_name = "";
                    for(let j = 0; j < data.roteiros[i].points.length; j++) {
                        arrayPoints = data.roteiros[i].points;
                        points_name += data.roteiros[i].points[j].title;
                        new Ponto(data.roteiros[i].points[j].latitude, data.roteiros[i].points[j].longitude,
                            data.roteiros[i].points[j].pointId, data.roteiros[i].points[j].title, data.roteiros[i].points[j].position);
                    }

                    div_rota.innerHTML += ` <div id="linha_rota" class="linha_rota">
                                                <div id="foto_rota" class="foto_rota">
                                                    <img src="img/bola.png" style="width: 50px; height: 50px;">
                                                </div>
                                                <div id="info_rota" >
                                                    <p id="rota_title" class="route_bar_form_title_rota">${data.roteiros[i].title}</p>
                                                    
                                                </div> 
                                                <div id="rota_desenhar"  style=" margin-left: 25px;">
                                                    <div onclick="show_route(${data.roteiros[i].roteiroId})"><img src="img/walk.png" alt="Get The Route" style="width: 20px; height: 20px;"></div>
                                                    <div id="${div_detail_name}" onclick="route_detail('${data.roteiros[i].roteiroId}')"><img src="img/duvida.png" alt="Detail The Route" style="width: 20px; height: 20px;"></div>
                                                </div>
                                            </div>`;
                    routesMap.set(data.roteiros[i].roteiroId, data.roteiros[i]);
                    for(let j = 0; j < data.roteiros[i].points.length; j++) {
                        arrayPoints = data.roteiros[i].points;
                        new Ponto(data.roteiros[i].points[j].latitude, data.roteiros[i].points[j].longitude,
                            data.roteiros[i].points[j].pointId, data.roteiros[i].points[j].title, data.roteiros[i].points[j].position);
                    }

                    mapaRotas.set(data.roteiros[i].roteiroId,arrayPoints);
                }

            }else{
                document.getElementById("route_list").innerHTML =  "No router found.";
                /*error_div_event("booming_places","Any route found for this search");*/

            }
            document.getElementById("route_list").style.display = 'block';
        })
        .catch(error => {
            console.log(error);
            /*error_div_event("booming_places", data.error);*/
            document.getElementById("route_list").innerHTML = "No router found.";

        });
}

function route_detail(route_id) {
    document.getElementById("route_detail").style.display = 'block';
    document.getElementById("route_list").style.display = 'none';
    data = routesMap.get(route_id+"");
    document.getElementById("title_rota_detail").innerHTML = data.title;
    if( data.description != undefined && data.description.length > 0 ){
        document.getElementById("rota_description_detail").innerHTML = data.description;
    }
    /*let content ="";*/
    /*for(let i = 0; i < data.points.length; i++) {
        content +=  data.points[i].title
        arrayPoints = data.roteiros[i].points;

        new Ponto(data.roteiros[i].points[j].latitude, data.roteiros[i].points[j].longitude,
            data.roteiros[i].points[j].pointId, data.roteiros[i].points[j].title, data.roteiros[i].points[j].position);
    }*/

    const element = document.getElementById("rota_desenhar_info");
    element.addEventListener("click", () => {
        show_route(data.roteiroId);
    });
    /*document.getElementById("rota_desenhar_info").onclick = show_route(data.roteiroId);
    *//*<div id="rota_desenhar" onClick="show_route("+routeId+")" style=" margin-left: 25px;">
        <img src="img/walk.png" alt="Get The Route" style="width: 20px; height: 20px;"/>
    </div>*/
}