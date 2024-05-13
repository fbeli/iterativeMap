

function read_points(){

    let url = config.list_points_by_user + "?userId=" + accessToken;
    fetch(url)
    .then(response => response.json())
    .then(data => {
        console.log(data);
        data.forEach(point => {
            let marker = new mapboxgl.Marker()
            .setLngLat([point.longitude, point.latitude])
            .addTo(map);
        });
    });
}