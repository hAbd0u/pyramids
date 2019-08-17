function resize_place_magic() {
	
	var canvas_width = $('#particles-js').css('width');
	$('#particles-js').css('height', canvas_width);
	
	var radius = parseInt(canvas_width)/2;
	var image_width = parseInt($('.team-member').first().css('width'));
	
	var count = -7;
	
	$('#particles-js .team-member').each(function() {
		
		var obj = $(this);
		++count;

		obj.css('left', (radius-image_width/2 + Math.sin(Math.PI/6*count) * radius) + 'px');
		obj.css('top',  (radius-image_width/2 + Math.cos(Math.PI/6*count) * radius) + 'px');
				
		
	});

}


$(window).resize(function() {
	
	resize_place_magic();
	
});

$(document).ready(function() {
	
	resize_place_magic();
	
	particlesJS('particles-js', {
		"particles": {
			"number": {
				"value": 115,
				"density": {
					"enable": true,
					"value_area": 800
				}
			},
			"color": {
				"value": "#b4c0ea"
			},
			"shape": {
				"type": "circle",
				"stroke": {
					"width": 0,
					"color": "#000000"
				},
				"polygon": {
					"nb_sides": 5
				},
				"image": {
					"src": "img/github.svg",
					"width": 100,
					"height": 100
				}
			},
			"opacity": {
				"value": 0.5,
				"random": false,
				"anim": {
					"enable": false,
					"speed": 1,
					"opacity_min": 0.1,
					"sync": false
				}
			},
			"size": {
				"value": 5,
				"random": true,
				"anim": {
					"enable": false,
					"speed": 40,
					"size_min": 0.1,
					"sync": false
				}
			},
			"line_linked": {
				"enable": true,
				"distance": 150,
				"color": "#b4c0ea",
				"opacity": 0.4,
				"width": 1
			},
			"move": {
				"enable": true,
				"speed": 4,
				"direction": "none",
				"random": false,
				"straight": false,
				"out_mode": "out",
				"bounce": false,
				"attract": {
					"enable": false,
					"rotateX": 600,
					"rotateY": 1200
				}
			}
		},
		"interactivity": {
			"detect_on": "canvas",
			"events": {
				"onhover": {
					"enable": false,
					"mode": "repulse"
				},
				"onclick": {
					"enable": false,
					"mode": "push"
				},
				"resize": true
			},
			"modes": {
				"grab": {
					"distance": 400,
					"line_linked": {
						"opacity": 1
					}
				},
				"bubble": {
					"distance": 400,
					"size": 40,
					"duration": 2,
					"opacity": 8,
					"speed": 3
				},
				"repulse": {
					"distance": 200,
					"duration": 0.4
				},
				"push": {
					"particles_nb": 4
				},
				"remove": {
					"particles_nb": 2
				}
			}
		},
		"retina_detect": true
	});


});







