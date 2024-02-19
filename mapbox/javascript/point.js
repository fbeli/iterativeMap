function aprovarPoint(url){
    fetch(url , {
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        }
    })
        .then(response => response.json())
        .then(data => {
           console.log(data);
        })
        .catch(error => {
            console.log(error);
        });
}