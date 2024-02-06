function aprovarPoint(url){
    fetch(url)
        .then(response => response.json())
        .then(data => {
            // Handle the response data here
        })
        .catch(error => {
            // Handle any errors here
        });
}