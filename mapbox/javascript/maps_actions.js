// The value for 'accessToken' begins with 'pk...'
mapboxgl.accessToken = 'pk.eyJ1IjoiZmJlbGkiLCJhIjoiY2xyM3N2Z2NxMW1zaDJpbXlmN2tydXRwaCJ9.IKxTu4ci2HIp_yOdWhn8Rg';
let point;

let long_start_lisbon = -9.135905;
let lat_start_lisbon = 38.709844;
const map = new mapboxgl.Map({
    container: 'map',
    style: 'mapbox://styles/fbeli/clr407wxw018b01r5bt6oht1o',
    center: [long_start_lisbon, lat_start_lisbon],
    zoom: defaultZoom,
    hash: true,

});
let map_lang = 'pt';

function change_language(){
    map.removeLayer('point_layer');
    map.removeLayer('cluster-count_layer');

    lang_selected = document.getElementById("loc_language").value;
    if(lang_selected == 'Português'){
        map_lang = 'pt';
    }else if(lang_selected == 'Spanish'){
        map_lang = 'sp';
    }else if(lang_selected == 'English'){
        map_lang = 'en';
    }
    add_layers();
}

function add_layers(){
    map.addLayer({
        'id': 'point_layer',
        'type': 'symbol',
        'layout': {
            'icon-image': 'bola',
            'icon-size': 0.1
        },
        'source': 's_mapfile_'+map_lang
    });
    map.addLayer({
        id: 'cluster-count_layer',
        type: 'symbol',
        source: 's_mapfile_'+map_lang,
        filter: ['has', 'point_count'],
        paint: {
            'text-color': '#fff',
            'icon-opacity': 0.6,
        },
        layout: {
            'icon-image': 'bola',
            'icon-size': 0.2,
            'text-field': ['get', 'point_count_abbreviated'],
            'text-font': ['DIN Offc Pro Medium', 'Arial Unicode MS Bold'],

        }
    });

}
map.on('load', () => {
    map.addSource('s_mapfile_pt', {
        type: 'geojson',
        // Use a URL for the value for the data property.
        data: 'https://www.guidemapper.com/file/mapFile_pt_.geojson',
        cluster: true,
        clusterMaxZoom: 19, // Max zoom to cluster points on
        clusterRadius: 10, // Radius of each cluster when clustering points (defaults to 50)
        clusterMinPoints: 2
    });
    map.addSource('s_mapfile_en', {
        type: 'geojson',
        // Use a URL for the value for the data property.
        data: 'https://www.guidemapper.com/file/mapFile_en_.geojson',
        cluster: true,
        clusterMaxZoom: 19, // Max zoom to cluster points on
        clusterRadius: 10, // Radius of each cluster when clustering points (defaults to 50)
        clusterMinPoints: 2
    });
    map.addSource('s_mapfile_lisboasecreta', {
        type: 'geojson',
        // Use a URL for the value for the data property.
        data: 'https://www.guidemapper.com/file/mapFile_lisboasecreta_.geojson'
    });
    map.addSource('s_mapfile_sp', {
        type: 'geojson',
        // Use a URL for the value for the data property.
        data: 'https://www.guidemapper.com/file/mapFile_sp_.geojson',
        cluster: true,
        clusterMaxZoom: 19, // Max zoom to cluster points on
        clusterRadius: 10, // Radius of each cluster when clustering points (defaults to 50)
        clusterMinPoints: 2
    });

    add_layers();
    show_zoom();

});
map.addControl(
    new MapboxGeocoder({
        accessToken: mapboxgl.accessToken,
        mapboxgl: mapboxgl
    }), "bottom-right"
);

map.addControl(new mapboxgl.GeolocateControl({
    positionOptions: {
        enableHighAccuracy: true
    },
    trackUserLocation: true,
    showUserHeading: true
}), "bottom-right");

/*
Add an event listener that runs
  when a user clicks on the map element.
*/
map.on('click', (event) => {
    // If the user clicked on one of your markers, get its information.
    const features = map.queryRenderedFeatures(event.point, {
        layers: ['cluster-count_layer', 'point_layer'] // replace with your layer name
    });
    fechar_divs();
    if (!features.length) {
        myLocation();
        if (map.getZoom() > zoom_to_create_point && create_point) {
            cadastro();
            point = event.lngLat;
            document.getElementById("cadastro_lng").value = "Longitude: " + point.lng;
            document.getElementById("cadastro_lat").value = "Latitude: " + point.lat;
        }

    } else {
        const feature = features[0];

        setLatLong(feature.geometry.coordinates);
        if(feature.layer.id.indexOf("cluster") == -1){

            console.log(feature.properties.pointId + " " + feature.properties.title);

            show_infos(feature);
        }else{
            map.flyTo({center: [feature.geometry.coordinates[0],feature.geometry.coordinates[1]], zoom: map.getZoom()+2 });
        }
    }
});

function show_zoom(){
    zoom = map.getZoom();
    document.getElementById("zoom").value = "Zoom: " + zoom;
}
map.on('move', () => {
    show_zoom();
})

class User {
    constructor(email, nome, instagram, photo, phone, share, guide) {
        this.email = email;
        this.name = nome;
        this.instagram = instagram;
        this.photo = photo;
        this.phone = phone;
        this.share = share;
        this.guide = true;
        if (guide !== undefined)
            this.guide = guide;

    }
}

async function getUser(id) {
    await fetch(config.get_user + id)
        .then(response => response.json())
        .then(data => {
            user = new User(data.email, data.name, data.instagram, data.photo, data.phone, data.share, data.guide);
            show_guide(user);

        });


}

function show_guide(user) {
    let include = `${user.instagram}`;
    if (user.guide === 'true') {
        include = `${user.email} <br> ${user.phone}`
    }

    document.getElementById("sh_professional").innerHTML = include;
}

function setLatLong(latLong) {
    save_cookies("latitude", latitude);
    save_cookies("longitude", longitude);
}

function myLocation() {
    save_cookies("latitude", map.getCenter().lat);
    save_cookies("longitude", map.getCenter().lng);
}

let atual_pos;

function getAtualLocation() {
    if ('geolocation' in navigator) {
        setTimeout(function () {
            navigator.geolocation.watchPosition(
                function (position) {
                    atual_pos = position.coords.latitude + "," + position.coords.longitude;
                },
                function (error) {
                    console.log(error);

                });
        }, 5000);
    } else {
        alert("Please, give us access to your geolocation");
    }
    return atual_pos;
}


async function getRoute(end) {
    // make a directions request using cycling profile
    // an arbitrary start will always be the same
    // only the end or destination will change
    // https://api.mapbox.com/directions/v5/mapbox/cycling/-122.42,37.78;-77.03,38.91?access_token=pk.eyJ1IjoiZmJlbGkiLCJhIjoiY2xyM3N2Z2NxMW1zaDJpbXlmN2tydXRwaCJ9.IKxTu4ci2HIp_yOdWhn8Rg

    atual_pos = await getAtualLocation();

    let position_str= ""
        if(end.length < 2){
            position_str = atual_pos.split(',')[1]+`,`+atual_pos.split(',')[0]+`;`+end[0].longitude+`,`+end[0].latitude;
        }else{
            for(let i=0; i<end.length; i++){
                position_str = position_str+end[i].longitude+`,`+end[i].latitude;
                if(i < end.length-1){
                    position_str = position_str+`;`;
                }
            }
        }
    const query = await fetch(
        `https://api.mapbox.com/directions/v5/mapbox/walking/`+position_str+`?steps=true&geometries=geojson&access_token=`+mapboxgl.accessToken,
        {method: 'GET'}
    );
    const json = await query.json();
    const data = json.routes[0];
    const route = data.geometry.coordinates;
    const geojson = {
        type: 'Feature',
        properties: {},
        geometry: {
            type: 'LineString',
            coordinates: route
        }
    };
    // if the route already exists on the map, we'll reset it using setData
    if (map.getSource('route')) {
        map.getSource('route').setData(geojson);
    }
    // otherwise, we'll make a new request
    else {
        map.addLayer({
            id: 'route',
            type: 'line',
            source: {
                type: 'geojson',
                data: geojson
            },
            layout: {
                'line-join': 'round',
                'line-cap': 'round'
            },
            paint: {
                'line-color': '#3887be',
                'line-width': 5,
                'line-opacity': 0.75
            }
        });
    }
    // add turn instructions here at the end
}

let coords
function draw_route() {
    const end = {
        type: 'FeatureCollection',
        features: [
            {
                type: 'Feature',
                properties: {},
                geometry: {
                    type: 'Point',
                    coordinates: coords
                }
            }
        ]
    };
    if (map.getLayer('end')) {
        map.getSource('end').setData(end);
    } else {
        map.addLayer({
            id: 'end',
            type: 'circle',
            source: {
                type: 'geojson',
                data: {
                    type: 'FeatureCollection',
                    features: [
                        {
                            type: 'Feature',
                            properties: {},
                            geometry: {
                                type: 'Point',
                                coordinates: coords
                            }
                        }
                    ]
                }
            },
            paint: {
                'circle-radius': 5,
                'circle-color': '#4c4444'
            }
        });
    }
    getRoute(coords);

    if(coords.length > 2){
        map.flyTo({center: [coords[0].longitude, coords[0].latitude], zoom: defaultZoom });
    }else {
        if (atual_pos === undefined)
            getAtualLocation();
        map.flyTo({center: [atual_pos.split(',')[1], atual_pos.split(',')[0]], zoom: defaultZoom - 2});
    }
    fechar_divs();
}

function show_route(roteiroId){
    roteiroId = ''+roteiroId;
    coords = mapaRotas.get(roteiroId)
    draw_route();
}

function create_path(){
    ponto  = new Ponto();
    ponto.longitude = coords[0];
    ponto.latitude = coords[1];

    coords = [];
    coords[0] = ponto;
    draw_route();

}
getAtualLocation();