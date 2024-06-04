// The value for 'accessToken' begins with 'pk...'
mapboxgl.accessToken = 'pk.eyJ1IjoiZmJlbGkiLCJhIjoiY2xyM3N2Z2NxMW1zaDJpbXlmN2tydXRwaCJ9.IKxTu4ci2HIp_yOdWhn8Rg';
let point;

let long_start_lisbon = -9.135905;
let lat_start_lisbon = 38.709844;
const map = new mapboxgl.Map({
    container: 'map',
    // Replace YOUR_STYLE_URL with your style URL.
    style: 'mapbox://styles/fbeli/clr407wxw018b01r5bt6oht1o',
    center: [long_start_lisbon, lat_start_lisbon],
    zoom: defaultZoom,
    hash: true,

});

map.on('load', () => {
    map.addSource('s_mapfile_pt', {
        type: 'geojson',
        // Use a URL for the value for the data property.
        data: 'https://www.guidemapper.com/file/mapFile_pt_.geojson'
    });
    map.addSource('s_mapfile_en', {
        type: 'geojson',
        // Use a URL for the value for the data property.
        data: 'https://www.guidemapper.com/file/mapFile_en_.geojson'
    });
    map.addSource('s_mapfile_lisboasecreta', {
        type: 'geojson',
        // Use a URL for the value for the data property.
        data: 'https://www.guidemapper.com/file/mapFile_lisboasecreta_.geojson'
    });
    map.addSource('s_mapfile_sp', {
        type: 'geojson',
        // Use a URL for the value for the data property.
        data: 'https://www.guidemapper.com/file/mapFile_sp_.geojson'
    });


    map.addLayer({
        'id': 'l_mapFile_pt',
        'type': 'symbol',
        'layout': {
            'icon-image': 'pt',
            'icon-size': 0.2
        },
        'source': 's_mapfile_pt'
    });

    map.addLayer({
        'id': 'l_mapFile_en',
        'type': 'symbol',
        'layout': {
            'icon-image': 'en',
            'icon-size': 0.2
        },
        'source': 's_mapfile_en'
    });
    map.addLayer({
        'id': 'l_lisboa_secreta',
        'type': 'symbol',
        'layout': {
            'icon-image': 'lisboa_secreta',
            'icon-size': 0.2
        },
        'source': 's_mapfile_lisboasecreta'
    });
    map.addLayer({
        'id': 'l_mapFile_sp',
        'type': 'symbol',
        'layout': {
            'icon-image': 'sp',
            'icon-size': 0.2
        },
        'source': 's_mapfile_sp'
    });

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
        layers: ['l_mapFile_pt', 'l_mapFile_en', 'l_lisboa_secreta', 'l_mapFile_sp'] // replace with your layer name

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
        console.log(feature.properties.pointId + " " + feature.properties.title);

        show_infos(feature);
    }
});


map.on('move', () => {
    zoom = map.getZoom();
    if (document.getElementById("inside_zoom") != null)
        document.getElementById("inside_zoom").innerHTML = zoom;
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
    //longitude = latLong[0];
    //latitude = latLong[1];
    save_cookies("latitude", latitude);
    save_cookies("longitude", longitude);
}

//const element = document.getElementById("mapboxgl-ctrl-geolocate");
//element.addEventListener("click", myFunction);
function myLocation() {
    save_cookies("latitude", map.getCenter().lat);
    save_cookies("longitude", map.getCenter.lng);
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

// create a function to make a directions request
async function getRoute(end) {
    // make a directions request using cycling profile
    // an arbitrary start will always be the same
    // only the end or destination will change
    // https://api.mapbox.com/directions/v5/mapbox/cycling/-122.42,37.78;-77.03,38.91?access_token=pk.eyJ1IjoiZmJlbGkiLCJhIjoiY2xyM3N2Z2NxMW1zaDJpbXlmN2tydXRwaCJ9.IKxTu4ci2HIp_yOdWhn8Rg

    atual_pos = await getAtualLocation();

    const query = await fetch(
        `https://api.mapbox.com/directions/v5/mapbox/walking/${atual_pos.split(',')[1]},${atual_pos.split(',')[0]};${end.split(',')[0]},${end.split(',')[1]}?steps=true&geometries=geojson&access_token=${mapboxgl.accessToken}`,
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

function draw_route(){
    draw_route(coords)
}

function draw_route(coords) {
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
                'circle-radius': 10,
                'circle-color': '#f30'
            }
        });
    }
    getRoute(coords);
    map.flyTo({center: [getLongitude(), getLatitude()], zoom: defaultZoom});
    fechar_divs();
}

getAtualLocation();