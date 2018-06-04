(function($) {
    "use strict";
    jQuery(document).ready(function($) {
        /*-------------------------------
        STICKY NAVIGATION
        ---------------------------------*/
        $('.header-bottom').sticky({
            topSpacing: 0
        });
        /*-------------------------------
        BOOTSTARP SCROLLSPY
        ---------------------------------*/
        $('body').scrollspy({
            target: '.navbar-collapse',
            offset: 95
        });
        /*-------------------------------
        SMOTH SCROLLING
        ---------------------------------*/
        $('li.smooth-menu a').bind("click", function(event) {
            var $anchor = $(this);
            var headerH = '70';
            $('html,body').stop().animate({
                scrollTop: $($anchor.attr('href')).offset().top - headerH + "px"
            }, 1200, 'easeInOutExpo');
            event.preventDefault();
        });
        /*-------------------------------
        NEWS TICKER
        ---------------------------------*/
        $.simpleTicker($('#rss-item'), {
            speed: 1000,
            delay: 3000,
            easing: 'swing',
            effectType: 'roll'
        });
        /*-------------------------------
        SLIDER CAROUSEL
        ---------------------------------*/
        $('#slider-area').owlCarousel({
            items: 1,
            dots: false,
            loop: true,
            nav: true,
            autoplay: true,
            animateOut: 'fadeOut',
            animateIn: 'fadeIn',
            navText: ['<i class="fa fa-long-arrow-left"></i>', '<i class="fa fa-long-arrow-right"></i>']
        });
        /*-------------------------------
        SLIDER EFFECT
        ---------------------------------*/
        $('#slider-area').on("translate.owl.carousel", function(e) {
            $(".single-slide h2, .single-slide p").removeClass("animated fadeInUp").css("opacity", "0");
            $(".single-slide .btn-filled,.single-slide .gr-btn").removeClass("animated fadeInDown").css("opacity", "0");
        });

        $('#slider-area').on("translated.owl.carousel", function(e) {
            $(".single-slide h2, .single-slide p").addClass("animated fadeInUp").css("opacity", "1");
            $(".single-slide .btn-filled,.single-slide .gr-btn").addClass("animated fadeInDown").css("opacity", "1");
        });
        /*-------------------------------
        SLIDER CAROUSEL
        ---------------------------------*/
        $('.slider-wrap').owlCarousel({
            autoPlay: false, //Set AutoPlay to 3 seconds
            items: 1,
            pagination: false,
            navigation: true,
            navigationText: ['<i class="fa fa-angle-left"></i>', '<i class="fa fa-angle-right"></i>']
        });
        /*-------------------------------
        PORTFOLIO ISOTOPE INT
        ---------------------------------*/
        $('.gallery-grid').isotope({
            itemSelector: '.single-gallery',
            layoutMode: 'fitRows'
        });

        /*-------------------------------
        PORTFOLIO ISOTOPE CLICK FUNC
        ---------------------------------*/
        $('.gallery-menu li').on("click", function(event) {
            $(".gallery-menu li").removeClass("active");
            $(this).addClass("active");
            var selector = $(this).attr('data-filter');
            $(".gallery-grid").isotope({
                filter: selector,
                animationOptions: {
                    duration: 750,
                    easing: 'linear',
                    queue: false,
                }
            });
            return false;
        });
        /*-------------------------------
        LIGHTBOX
        ---------------------------------*/
        $('a.pretty').prettyPhoto({
            default_width: 500,
            default_height: 344,
        });

        /*-------------------------------
        Service Carousel
        ---------------------------------*/
        $('.testimonial-wrap').owlCarousel({
            autoPlay: true, //Set AutoPlay to 3 seconds
            items: 1,
            dots: true,
            nav: false,
        });
        $('#ajax-contact .btn-filled').on("click", function(e){
            $('#ajax-contact').hide('slow');
        });

    });

    jQuery(window).on('load',function(e) {
        /*-------------------------------
        PRELOADER
        ---------------------------------*/
        $('#pureheart-preloader-container').fadeOut('slow');
    });

}(jQuery));
