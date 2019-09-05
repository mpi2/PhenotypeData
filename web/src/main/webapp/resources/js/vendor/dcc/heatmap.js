/* Copyright 2013 Medical Research Council Harwell */
(function() {
        if (typeof dcc === "undefined") {
            dcc = {}
        }
        var G = "1.3.1"
            , Q = /^[-+]?[0-9]*\.?[0-9]+([eE][-+]?[0-9]+)?$/
            , ao = /^#?([0-9a-zA-Z]{2})([0-9a-zA-Z]{2})([0-9a-zA-Z]{2})$/
            , D = /^#?([0-9a-zA-Z])([0-9a-zA-Z])([0-9a-zA-Z])$/
            , X = /^rgba?\((\d+),[\s\xa0]*(\d+),[\s\xa0]*(\d+)(?:,[\s\xa0]*(0\.\d+))?\)$/
            , U = "/phenoview/rest/measurements/download/significant/parameters/?mgiid="
            , P = false
            , T = window.document.body
            , ae = T.onselectstart
            , s = T.onmouseup
            , f = ["#0099ff", "#ebf7ff", "#ff6600", "#ffffff", "#1f77b4", "#aec7e8", "#ff7f0e", "#ffbb78", "#2ca02c", "#98df8a", "#d62728", "#ff9896", "#9467bd", "#c5b0d5", "#8c564b", "#c49c94", "#e377c2", "#f7b6d2", "#7f7f7f", "#c7c7c7", "#bcbd22", "#dbdb8d", "#17becf", "#9edae5"]
            , ac = 131072
            , an = 262144
            , ar = 524288
            , A = ac | an | ar
            , aj = 3144161
            , b = aj ^ A
            , J = 0.0001;
        function q(ay, ax, aw) {
            var av = new Date();
            av.setTime(av.getTime() + (aw * 24 * 60 * 60 * 1000));
            var au = "expires=" + av.toUTCString();
            document.cookie = ay + "=" + (ax ? ax : "") + "; " + au
        }
        function l(ay) {
            var au = ay + "=";
            var ax = document.cookie.split(";");
            for (var aw = 0; aw < ax.length; aw++) {
                var av = ax[aw];
                while (av.charAt(0) === " ") {
                    av = av.substring(1)
                }
                if (av.indexOf(au) === 0) {
                    return av.substring(au.length, av.length)
                }
            }
            return undefined
        }
        Object.keys = Object.keys || function(aw) {
            var au = [];
            for (var av in aw) {
                if (aw.hasOwnProperty(av)) {
                    au.push(av)
                }
            }
            return au
        }
        ;
        function H(au) {
            return au === undefined ? undefined : au.k
        }
        function M(au) {
            return au.v
        }
        function R(au) {
            return au.u
        }
        function z(au, av) {
            return au[av]
        }
        function K(au) {
            throw new Error(au)
        }
        function w(aw, av, au) {
            if (aw.attachEvent) {
                aw.attachEvent("on" + av, au)
            } else {
                if (aw.addEventListener) {
                    aw.addEventListener(av, au, false)
                }
            }
        }
        function ai(au) {
            if (au.preventDefault) {
                au.preventDefault()
            }
            if (au.stopPropagation) {
                au.stopPropagation()
            }
            au.cancelBubble = true;
            return false
        }
        function m(au, av) {
            var aw = new XMLHttpRequest();
            aw.open("GET", au, true);
            aw.onreadystatechange = function(ax) {
                if (aw.readyState === 4) {
                    if (aw.status === 200) {
                        av(JSON.parse(aw.responseText))
                    } else {
                        K("Unable to retrieve data from " + au)
                    }
                }
            }
            ;
            aw.send(null)
        }
        String.prototype.discard = function(av) {
            var au = this.length - av;
            return av < 0 ? this.substr(0, au) : this.substr(av, au)
        }
        ;
        function d(av, ax) {
            var aw = ""
                , au = document.defaultView;
            if (au && au.getComputedStyle) {
                aw = au.getComputedStyle(av, "").getPropertyValue(ax)
            } else {
                if (av.currentStyle) {
                    ax = ax.replace(/\-(\w)/g, function(az, ay) {
                        return ay.toUpperCase()
                    });
                    aw = av.currentStyle[ax]
                }
            }
            return aw
        }
        function I(av, au, aw) {
            if (av && au) {
                if (aw === undefined) {
                    return av.getAttribute(au)
                } else {
                    return av.setAttribute(au, aw)
                }
            }
            return null
        }
        function E(au, aw, av) {
            if (av === undefined) {
                return d(au, aw)
            } else {
                au.style[aw] = av;
                return av
            }
        }
        function y(au, av) {
            return E(au, "background-color", av)
        }
        function i(au, av) {
            return E(au, "background", av)
        }
        function j(au, av) {
            return E(au, "backgroundImage", av)
        }
        function ab(au, av) {
            if (av) {
                au.innerHTML = av
            }
            return au.innerHTML
        }
        function af(au, aw, av) {
            if (av === undefined) {
                return parseInt(d(au, aw).discard(-2))
            } else {
                au.style[aw] = av + "px";
                return av
            }
        }
        function aq(av, au) {
            return af(av, "height", au)
        }
        function S(av, au) {
            return af(av, "width", au)
        }
        function B(au, av) {
            return af(au, "top", av)
        }
        function u(au, av) {
            return af(au, "left", av)
        }
        function ad(av, au, aw) {
            return af(av, "padding-" + au, aw)
        }
        function h(av, au, aw) {
            return af(av, "margin-" + au, aw)
        }
        function O(au, av) {
            if (av !== undefined) {
                au.scrollLeft = av
            }
            return au.scrollLeft
        }
        function x(au, av) {
            if (av !== undefined) {
                au.scrollTop = av
            }
            return au.scrollTop
        }
        function ak(av, au) {
            if (au !== undefined) {
                av.scrollHeight = au
            }
            return av.scrollHeight
        }
        function aa(au) {
            var av = 0;
            while (au && am(au) !== "dcc-heatmap-root") {
                av += au.offsetTop;
                au = au.offsetParent
            }
            return av
        }
        function k(au) {
            var av = 0;
            while (au && am(au) !== "dcc-heatmap-root") {
                av += au.offsetLeft;
                au = au.offsetParent
            }
            return av
        }
        function am(av, au) {
            if (au === undefined) {
                return av.getAttribute("class")
            } else {
                av.setAttribute("class", au);
                return au
            }
        }
        function at(au) {
            return au.pageX === undefined ? au.clientX : au.pageX
        }
        function ap(au) {
            return au.pageY === undefined ? au.clientY : au.pageY
        }
        function Y(au) {
            return au.clientX
        }
        function W(au) {
            return au.clientY
        }
        function t() {
            return window.innerWidth || document.documentElement.clientWidth
        }
        function p() {
            return window.innerHeight || document.documentElement.clientHeight
        }
        function ah(aw, av, au) {
            clearTimeout(aw.throttleTimeout);
            aw.throttleTimeout = setTimeout(function() {
                aw.apply(au)
            }, av)
        }
        function ag(au) {
            if (au === undefined) {
                return
            }
            while (au.firstChild) {
                ag(au.firstChild);
                au.removeChild(au.firstChild)
            }
        }
        function n(aw, au, az, av, ay) {
            var ax = document.createElement(au);
            if (aw) {
                aw.appendChild(ax)
            } else {
                K("Parent node required")
            }
            if (az) {
                ax.setAttribute("id", az)
            }
            if (av) {
                ax.setAttribute("class", av)
            }
            if (ay !== undefined) {
                ax.innerHTML = ay
            }
            return ax
        }
        function C(au, aw, av) {
            return au + av * (aw - au)
        }
        function r(au) {
            return Math.min(255, Math.max(0, Math.round(au)))
        }
        function a(au, aw, av) {
            return {
                r: r(C(au.r, aw.r, av)),
                g: r(C(au.g, aw.g, av)),
                b: r(C(au.b, aw.b, av))
            }
        }
        function N(av) {
            var ax = av.r.toString(16)
                , aw = av.g.toString(16)
                , au = av.b.toString(16);
            return "#" + (ax.length === 1 ? "0" : "") + ax + (aw.length === 1 ? "0" : "") + aw + (au.length === 1 ? "0" : "") + au
        }
        function c(aw) {
            var au = aw.match(X), av;
            if (au === null) {
                au = aw.match(ao);
                if (au === null) {
                    au = aw.match(D);
                    if (au === null) {
                        K("Invalid colour: '" + aw + "'")
                    } else {
                        av = {
                            r: parseInt(au[1] + au[1], 16),
                            g: parseInt(au[2] + au[1], 16),
                            b: parseInt(au[3] + au[1], 16),
                            a: undefined
                        }
                    }
                } else {
                    av = {
                        r: parseInt(au[1], 16),
                        g: parseInt(au[2], 16),
                        b: parseInt(au[3], 16),
                        a: undefined
                    }
                }
            } else {
                av = {
                    r: parseInt(au[1]),
                    g: parseInt(au[2]),
                    b: parseInt(au[3]),
                    a: au[4] === undefined ? undefined : parseFloat(au[4])
                }
            }
            return av
        }
        function F(au) {
            var av = new Date().getTime();
            au();
            return new Date().getTime() - av
        }
        function o(au) {
            return Math.LOG10E * Math.log(au)
        }
        function g(au) {
            if (!au) {
                au = window.event
            }
            return au
        }
        function e(aw, av) {
            var au = "dcc-heatmap-popup-"
                , ax = n(n(n(aw, "table"), "thead"), "tr");
            if (av) {
                au += "ont-";
                n(ax, "th", null, au + "hp", "Procedure")
            }
            n(ax, "th", null, au + "hq", "Parameter");
            n(ax, "th", null, au + "ht", "MP annotation");
            n(ax, "th", null, au + "hi", "");
            n(ax, "th", null, au + "hv", "p-value")
        }
        function al(ay, aw, av) {
            if (av === undefined) {
                return
            }
            var az = "dcc-heatmap-popup-", aB, aC, ax, au, aA = "";
            aB = n(aw, "div", null, az + "toolbar");
            n(aB, "div", null, az + "toolbar-label", "Visualise:");
            ax = n(aB, "div", null, az + "toolbar-button", "All parameters");
            aC = n(aB, "div", null, az + "toolbar-button", "All significant parameters");
            for (au in av) {
                aA += av[au].k + ","
            }
            ax.onclick = ay.getPopupToolbarOnClickHandler(ay.popupType);
            aC.onclick = ay.getPopupToolbarOnClickHandler(aA.substring(0, aA.length - 1))
        }
        function L(av) {
            var au = null;
            switch (av) {
                case 1:
                    au = "increased";
                    break;
                case 2:
                    au = "decreased";
                    break;
                case 3:
                    au = "abnormal";
                    break;
                case 4:
                    au = "inferred";
                    break
            }
            return "dcc-heatmap-popup-icon dcc-heatmap-popup-icon-" + au
        }
        function v(av, au) {
            return (av === undefined || av < 0 || av > 1) ? undefined : av === 1 ? 1 : av.toPrecision(au === undefined ? 5 : au)
        }
        T.onmouseup = function(au) {
            P = false;
            T.onselectstart = ae;
            if (s) {
                s(g(au))
            }
        }
        ;
        var V = function(aB, au, aA, ax, az, aD, av, aw, aC, ay) {
            this.id = au;
            this.minValue = ax;
            this.maxValue = az;
            this.valueRange = az - ax;
            this.defaultValue = ay === undefined ? 0.5 * (this.maxValue - this.minValue) : ay;
            if (aC === undefined || aC < ax || aC > az) {
                this.initialValue = ay
            } else {
                this.initialValue = aC
            }
            this.onValueChange = aw;
            this.sliderHeight = aD;
            this.sliderWidth = av;
            this.labelText = aA;
            this.renderSlider(aB)
        };
        V.prototype = {
            getSliderValue: function() {
                var av = this
                    , au = av.value.value;
                if (Q.test(au)) {
                    au = parseFloat(au)
                } else {
                    K("Invalid threshold value")
                }
                return au
            },
            positionFromValue: function() {
                var au = this
                    , aw = au.value
                    , av = aw.value;
                if (Q.test(av)) {
                    E(aw, "color", "#000000");
                    if (av > 0 && av < 1) {
                        av = 0.1 * o(parseFloat(av)) + 1
                    }
                    if (av < au.minValue) {
                        av = au.minValue;
                        E(aw, "color", "green")
                    } else {
                        if (av > au.maxValue) {
                            av = au.maxValue;
                            E(aw, "color", "blue")
                        }
                    }
                } else {
                    E(aw, "color", "#ff0000");
                    av = au.defaultValue
                }
                return au.minButtonLeft + au.barWidth * (av - au.minValue) / au.valueRange
            },
            valueFromPosition: function() {
                var au = this, av;
                E(au.value, "color", "#000000");
                av = au.minValue + au.valueRange * (u(au.button) + 0.5 * au.buttonWidth - au.barLeft) / au.barWidth;
                if (av > 0 && av < 1) {
                    av = Math.pow(10, (av - 1) * 10)
                }
                return v(av)
            },
            getDragStartHandler: function(au) {
                return function(av) {
                    P = true;
                    av = g(av);
                    au.displacement = u(au) - at(av);
                    T.onselectstart = function() {
                        return false
                    }
                }
            },
            getDragHandler: function(au) {
                var av = this;
                return function(ax) {
                    ax = g(ax);
                    if (P) {
                        var aw = at(ax) + au.displacement;
                        if (aw >= av.minButtonLeft && aw <= av.maxButtonRight) {
                            u(au, aw);
                            av.value.value = av.valueFromPosition();
                            if (av.onValueChange) {
                                av.onValueChange(av.value.value)
                            }
                        }
                    }
                }
            },
            attachSliderDragHandler: function() {
                var ax = this
                    , ay = ax.range
                    , av = ax.button
                    , au = ax.getDragStartHandler(av)
                    , aw = ax.getDragHandler(av);
                w(av, "touchstart", au);
                av.onmousedown = au;
                w(ay, "touchmove", aw);
                ay.onmousemove = aw
            },
            refitSlider: function() {
                var aP = this
                    , aQ = aP.sliderHeight * 0.5
                    , aM = S(aP.label)
                    , av = aq(aP.label)
                    , aN = aQ - av * 0.5
                    , aG = S(aP.value) + ad(aP.value, "left") + ad(aP.value, "right") + h(aP.value, "left") + h(aP.value, "right")
                    , ax = aq(aP.value) + ad(aP.value, "top") + ad(aP.value, "bottom")
                    , aR = aQ - ax * 0.5
                    , az = aP.sliderWidth - aM - aG - ad(aP.range, "left") - ad(aP.range, "right") - 2
                    , aw = 0
                    , aC = aP.sliderHeight
                    , aF = az
                    , aJ = aq(aP.bar)
                    , aI = aQ - aJ * 0.5
                    , aA = ad(aP.range, "left")
                    , aB = aq(aP.button)
                    , au = aQ - aB * 0.5
                    , aE = S(aP.min)
                    , ay = aq(aP.min)
                    , aD = aQ + aB * 0.5 + ay * 0.25
                    , aH = aA - aE * 0.5
                    , aL = S(aP.max)
                    , aO = aD
                    , aK = aA + az - aL * 0.5;
                aP.buttonWidth = S(aP.button);
                aP.halfButtonWidth = 0.5 * aP.buttonWidth;
                aP.barLeft = aA;
                aP.barRight = aA + aF;
                aP.barWidth = aF;
                aP.minButtonLeft = aA - aP.halfButtonWidth;
                aP.maxButtonRight = aP.barRight - aP.halfButtonWidth;
                aq(aP.slider, aP.sliderHeight);
                B(aP.label, aN);
                B(aP.value, aR);
                B(aP.range, aw);
                S(aP.range, az);
                aq(aP.range, aC);
                B(aP.bar, aI);
                S(aP.bar, aF);
                u(aP.bar, aA);
                B(aP.button, au);
                S(aP.button, aP.buttonWidth);
                B(aP.min, aD);
                u(aP.min, aH);
                B(aP.max, aO);
                u(aP.max, aK);
                return aP
            },
            renderSlider: function(au) {
                var av = this
                    , ax = av.id
                    , aw = "dcc-slider";
                av.slider = n(au, "div", ax, aw);
                av.label = n(av.slider, "div", ax + "-label", aw + "-label", av.labelText);
                av.value = n(av.slider, "input", ax + "-value", aw + "-value");
                av.value.onkeyup = function(ay) {
                    av.setValue(av.value.value)
                }
                ;
                av.value.value = av.initialValue;
                av.range = n(av.slider, "div", ax + "-range", aw + "-range");
                av.bar = n(av.range, "div", ax + "-bar", aw + "-bar");
                av.button = n(av.range, "div", ax + "-button", aw + "-button");
                av.reset = n(av.range, "div", ax + "-reset", aw + "-reset");
                I(av.reset, "title", "Reset slider value");
                w(av.reset, "click", function() {
                    av.setValue(av.defaultValue)
                });
                av.min = n(av.range, "div", ax + "-min", aw + "-min", av.minValue);
                av.max = n(av.range, "div", ax + "-max", aw + "-max", av.maxValue);
                av.refitSlider();
                av.attachSliderDragHandler(av);
                av.value.onkeyup();
                return av
            },
            setValue: function(av) {
                var au = this;
                au.value.value = av === undefined ? au.defaultValue : av;
                au.buttonLeft = au.positionFromValue();
                u(au.button, au.buttonLeft);
                au.onValueChange(au.value.value)
            },
            hideSlider: function() {
                E(this.slider, "visibility", "hidden")
            },
            showSlider: function() {
                E(this.slider, "visibility", "visible")
            }
        };
        function Z(au) {
            return au.substring(au.length - 1) !== "/" ? au + "/" : au
        }
        dcc.PhenoHeatMap = function(av) {
            var au = l("phenodcc_heatmap_mode");
            if (av === undefined) {
                K("Invalid heatmap properties")
            }
            this.version = G;
            if (!this.checkHostDivElement(av.container)) {
                return
            }
            this.id = av.container;
            if (av.mgiid === undefined || av.mgiid.length < 1) {
                K("Invalid MGI identifier")
            }
            this.mgiId = av.mgiid;
            if (au) {
                av.mode = au
            } else {
                if (av.mode !== "ontological" && av.mode !== "procedural") {
                    av.mode = "ontological"
                }
                q("phenodcc_heatmap_mode", av.mode)
            }
            this.isOntological = av.mode === "ontological";
            this.numColumns = (av.ncol === undefined ? 5 : av.ncol);
            if (av.url === undefined) {
                K("Invalid URL properties")
            }
            this.jssrc = av.url.jssrc === undefined ? "js/" : av.url.jssrc;
            this.jssrc = Z(this.jssrc);
            this.json = av.url.json === undefined ? "rest/" : av.url.json;
            this.json = Z(this.json);
            this.viz = function(az, ay, ax, aw) {
                return "/phenoview/?gid=" + az + "&qeid=" + ay + (ax === undefined ? "" : ("&ctrl=" + ax)) + (aw === undefined ? "" : ("&pt=" + aw))
            }
            ;
            this.rowFormatter = M;
            this.columnFormatter = R;
            if (av.format !== undefined) {
                if (av.format.row !== undefined) {
                    if (typeof av.format.row !== "function") {
                        K("Invalid row formatter")
                    } else {
                        this.rowFormatter = av.format.row
                    }
                }
                if (av.format.column !== undefined) {
                    if (typeof av.format.column !== "function") {
                        K("Invalid column formatter")
                    } else {
                        this.columnFormatter = av.format.column
                    }
                }
            }
            this.pvalueExtractor = z;
            this.keyExtractor = H;
            if (av.extract !== undefined) {
                if (av.extract.pvalue !== undefined) {
                    if (typeof av.extract.pvalue !== "function") {
                        K("Invalid p-value extractor")
                    } else {
                        this.pvalueExtractor = av.extract.pvalue
                    }
                }
                if (av.extract.key !== undefined) {
                    if (typeof av.extract.key !== "function") {
                        K("Invalid key extractor")
                    } else {
                        this.keyExtractor = av.extract.key
                    }
                }
            }
            this.windowRowStart = 0;
            this.windowRowEnd = 0;
            this.windowColumnStart = 0;
            this.windowColumnEnd = 0;
            this.data = null;
            this.dataRowHeaders = null;
            this.dataColumnHeaders = null;
            this.dataPvalues = null;
            this.dataNumRows = 0;
            this.dataNumColumns = 0;
            this.dataMinValue = null;
            this.dataMaxValue = null;
            this.dataValueRange = null;
            this.significantColour = null;
            this.insignificantColour = null;
            this.highlightedSignificantColour = null;
            this.highlightedInsignificantColour = null;
            this.significantColourChannels = null;
            this.insignificantColourChannels = null;
            this.highlightedSignificantColourChannels = null;
            this.highlightedInsignificantColourChannels = null;
            this.rowHeaderCellHeight = 47;
            this.columnHeaderCellWidth = 102;
            this.significant = null;
            this.insignificant = null;
            this.significantHighlighted = null;
            this.insignificantHighlighted = null;
            this.pegged = {};
            this.activePeg = {};
            this.breadcrums = [{
                l: "Overview"
            }];
            this.breadcrumsNode = null;
            this.currentPage = 0;
            this.cells = null;
            this.pvalueThreshold = 0;
            this.colourPicker = null;
            this.colourBoxIndex = 0;
            this.popuGid = null;
            this.popupType = null;
            this.includeBaseline = false;
            this.initialiseHeatmap()
        }
        ;
        dcc.PhenoHeatMap.prototype = {
            getVersion: function() {
                return this.version
            },
            updateUrls: function() {
                var au = this;
                if (au.isOntological) {
                    au.detailsUrl = au.heatmapUrl = au.json + "ontological/"
                } else {
                    au.detailsUrl = au.heatmapUrl = au.json + "procedural/"
                }
                au.heatmapUrl += "heatmap";
                au.detailsUrl += "details"
            },
            changeMode: function() {
                var au = this;
                au.type = undefined;
                au.isOntological = !au.isOntological;
                if (au.isOntological) {
                    q("phenodcc_heatmap_mode", "ontological")
                } else {
                    q("phenodcc_heatmap_mode", "procedural")
                }
                au.updateUrls();
                au.breadcrums = [{
                    l: "Overview"
                }];
                au.updateNavigationBar();
                au.retrieveSingleGeneData()
            },
            prepareColours: function() {
                var au = this;
                au.significantColour = y(au.significant);
                au.insignificantColour = y(au.insignificant);
                au.significantColourChannels = c(au.significantColour);
                au.insignificantColourChannels = c(au.insignificantColour)
            },
            addCellEventHandlers: function(aA, az, aw, av) {
                var ay = this, ax, au;
                if (az !== undefined && av) {
                    am(aA, "clickable");
                    ax = ay.getCellOnClickHandler(aw, av, az);
                    au = ay.getCellOnMouseoverHandler(aw, av);
                    aA.onmouseup = ax;
                    aA.onmouseover = au;
                    w(aA, "touchstart", function(aB) {
                        ai(aB);
                        aA.touchEvent = aB;
                        aA.touchStartTime = new Date()
                    });
                    w(aA, "touchend", function(aC) {
                        var aB = new Date();
                        ai(aC);
                        if (aB.getTime() - aA.touchStartTime.getTime() > 1000) {
                            ax(aA.touchEvent)
                        } else {
                            au(aA.touchEvent)
                        }
                    })
                }
            },
            prepareSingleGeneContent: function(au) {
                return this.content = n(au, "div", null, "dcc-heatmap-content")
            },
            pushBreadcrum: function(aw, au) {
                var ay = this
                    , ax = ay.breadcrums.length;
                for (var av = ax - 1; av > -1; --av) {
                    if (ay.breadcrums[av].k === aw) {
                        break
                    }
                }
                if (av > -1) {
                    while (++av < ax) {
                        ay.breadcrums.pop()
                    }
                } else {
                    ay.breadcrums.push({
                        k: aw,
                        l: au
                    })
                }
                ay.updateNavigationBar()
            },
            getHeatmapRetriever: function(av, au) {
                var aw = this;
                return function() {
                    aw.type = av;
                    aw.retrieveSingleGeneData(av, au)
                }
            },
            createCheckbox: function(aA, az, av, aB, au) {
                var aC = "dcc-heatmap-checkbox-"
                    , ax = aC + "selected"
                    , ay = aC + "unselected"
                    , aw = n(aA, "div", null, null, az);
                if (au) {
                    I(aw, "title", au)
                }
                if (av) {
                    aw.isSelected = true;
                    aC = ax
                } else {
                    aw.isSelected = false;
                    aC = ay
                }
                aw.className = aC;
                aw.onclick = function(aD) {
                    if (aw.isSelected) {
                        aw.isSelected = false;
                        aw.className = ay
                    } else {
                        aw.isSelected = true;
                        aw.className = ax
                    }
                    if (aB) {
                        aB()
                    }
                }
                ;
                return aw
            },
            selectColour: function(av, aw) {
                var au = this;
                switch (au.colourBoxIndex) {
                    case 0:
                        i(au.significant, aw);
                        break;
                    case 1:
                        i(au.insignificant, aw);
                        break;
                    case 2:
                        i(au.significantHighlighted, aw);
                        break;
                    case 3:
                        i(au.insignificantHighlighted, aw);
                        break;
                    default:
                        return
                }
                au.hideColourPicker();
                au.prepareColours()
            },
            getColourSelector: function(av) {
                var au = this;
                return function(aw) {
                    au.selectColour(g(aw), av);
                    au.updatePvalueSections()
                }
            },
            createColourPicker: function(aE) {
                var aC = this, az, aw, av, aB = 0, au, aF = 4, aD = 5, aA = document.createDocumentFragment(), ay = n(aA, "div", null, "dcc-heatmap-colour-picker"), ax = n(n(ay, "table"), "tbody");
                ay.onmouseover = function(aG) {
                    aG = g(aG);
                    ai(aG)
                }
                ;
                for (az = 0; az < aD; ++az) {
                    n(ax, "tr");
                    for (aw = 0; aw < aF; ++aw) {
                        av = n(ax, "td");
                        au = f[aB++];
                        if (au === undefined) {
                            E(av, "cursor", "default")
                        } else {
                            i(av, au);
                            av.onmouseup = aC.getColourSelector(au)
                        }
                    }
                }
                aE.appendChild(aA);
                aE.onmouseover = function() {
                    aC.hideColourPicker();
                    aC.colourBoxIndex = undefined
                }
                ;
                return aC.colourPicker = ay
            },
            showColourPicker: function(ay) {
                var ax = this, aw = ay.target, au, az, av = 10;
                if (dcc.ie8) {
                    au = Y(ay) - av;
                    az = W(ay) - av
                } else {
                    au = aw.offsetLeft;
                    az = aw.offsetTop
                }
                u(ax.colourPicker, au);
                B(ax.colourPicker, az);
                E(ax.colourPicker, "visibility", "visible")
            },
            hideColourPicker: function() {
                var au = this;
                E(au.colourPicker, "visibility", "hidden")
            },
            createLegend: function(az, ax, ay, aw) {
                var aA = this
                    , av = "dcc-heatmap-toolbar-legend-"
                    , au = n(az, "div", null, "dcc-heatmap-toolbar-legend")
                    , aB = n(au, "div", null, av + ay + "-colour");
                n(au, "div", null, av + "label", ax);
                if (aw !== undefined) {
                    aB.onmouseover = function(aC) {
                        aC = g(aC);
                        ai(aC);
                        aA.showColourPicker(aC);
                        aA.colourBoxIndex = aw
                    }
                }
                return aB
            },
            appendLegends: function(au) {
                var av = this;
                av.significant = av.createLegend(au, "Significant", "significant", 0);
                av.insignificant = av.createLegend(au, "Insignificant", "insignificant", 1);
                E(av.createLegend(au, "No data", "nodata"), "cursor", "default")
            },
            appendDownloaders: function(ax) {
                var ay = this, au, aw, aA, az, av;
                au = n(ax, "div", null, "dcc-heatmap-toolbar-downloader-container");
                az = ay.createCheckbox(au, "Include baseline", false, function() {
                    ay.includeBaseline = !ay.includeBaseline
                }, "Download takes a lot longer when\nbaseline measurements are included");
                aw = n(au, "div", null, "dcc-heatmap-toolbar-downloader-all", "Download all"),
                    aA = n(au, "div", null, "dcc-heatmap-toolbar-downloader-significant", "Download significant");
                av = n(au, "div", null, "dcc-heatmap-toolbar-downloader-help", "Help"),
                    I(aw, "title", "Download measurements for all parameters");
                I(aA, "title", "Download measurements for all significant\nparameters under the current p-value threshold");
                I(av, "title", "User manual that describes the response and\nhow the measurements should be interpreted");
                aw.onclick = function() {
                    window.open(U + ay.mgiId + "&pvalueThreshold=1&includeBaseline=" + ay.includeBaseline)
                }
                ;
                aA.onclick = function() {
                    window.open(U + ay.mgiId + "&pvalueThreshold=" + ay.pvalueThreshold + "&includeBaseline=" + ay.includeBaseline)
                }
                ;
                av.onclick = function() {
                    window.open("/phenoview/download.html")
                }
            },
            appendMeanings: function(aw) {
                var ax = this
                    , ay = function(aA, aB, aC, aE) {
                    var aD = n(aw, "div", null, "dcc-heatmap-toolbar-legend");
                    n(aD, "div", null, aA, aC);
                    n(aD, "div", null, "legends-meaning", aB);
                    aD.setAttribute("title", aE)
                }
                    , az = n(aw, "div", null, "dcc-top-legends")
                    , av = n(aw, "div", null, "dcc-middle-legends")
                    , au = n(aw, "div", null, "dcc-bottom-legends");
                aw = az;
                ay("legends-hit", "Phenodeviance detected", "", "At least one of the parameters under\nthis category detected zygosity phenodeviance");
                ay("legends-nohit", "No phenodeviance detected", "", "None of the parameters under\nthis category detected zygosity phenodeviance");
                ay("legends-zyg", "Homozygous", "Hom", "There is homozygous annotation");
                ay("legends-zyg", "Heterozygous", "Het", "There is heterozygous annotation");
                ay("legends-zyg", "Hemizygous", "Hem", "There is hemizygous annotation");
                ay("legends-sex", "Sexual dimorphism", "", "There is sexual dimorphism annotation");
                aw = av;
                ay("legends-popup-hit", "Zygosity phenodeviance", "", "Phenodeviance detected for this zygosity and parameter");
                ay("legends-popup-nohit", "No zygosity phenodeviance", "", "No phenodeviance detected for this zygosity and parameter");
                ay("legends-popup-sex-hit", "Sexual dimorphism detected", "", "Sexual dimorphism detected for this\nzygosity and parameter");
                ay("legends-popup-sex-nohit", "No sexual dimorphism detected", "", "No sexual dimorphism detected for\nthis zygosity and parameter");
                aw = au;
                ax.appendLegends(aw)
            },
            updateNavigationBar: function() {
                var aA = this, ay = 0, av = aA.breadcrums.length, aB, aD, az = document.createDocumentFragment(), aC = "dcc-heatmap-breadcrum-", ax = aC + "mode-", aw = ax + "active", au = ax + "inactive";
                if (aA.title) {
                    n(az, "div", null, aC + "gene", aA.title + " : Adult");
                    n(az, "div", null, aC + "gene-separator")
                }
                while (ay < av) {
                    aB = aA.breadcrums[ay++];
                    if (aB === undefined) {
                        continue
                    }
                    aD = n(az, "div", null, aC + "item", aB.l);
                    aD.onmouseup = aA.getHeatmapRetriever(aB.k, aB.l);
                    aD = n(az, "div", null, aC + "separator")
                }
                aA.ontological = n(az, "div", null, aA.isOntological ? aw : au, "Ontological");
                aA.procedural = n(az, "div", null, aA.isOntological ? au : aw, "Procedural");
                w(aA.ontological, "click", function() {
                    if (aA.isOntological) {
                        return
                    }
                    am(aA.procedural, au);
                    am(aA.ontological, aw);
                    aA.changeMode()
                });
                w(aA.procedural, "click", function() {
                    if (!aA.isOntological) {
                        return
                    }
                    am(aA.ontological, au);
                    am(aA.procedural, aw);
                    aA.changeMode()
                });
                w(n(az, "div", null, aC + "help", "Help"), "click", function(aE) {
                    aE = g(aE);
                    ai(aE);
                    window.open(aA.jssrc + "../manual.html")
                });
                ag(aA.breadcrumsNode);
                aA.breadcrumsNode.appendChild(az)
            },
            prepareToolbar: function(aB) {
                var az = this
                    , au = "dcc-heatmap-toolbar-"
                    , aw = l("phenodcc_heatmap_pvalue_threshold")
                    , aA = n(aB, "div", null, au + "toolbar")
                    , aC = n(aA, "div", null, au + "breadcrums")
                    , ax = n(aA, "div", null, au + "legends")
                    , ay = n(aA, "div", null, au + "pvaluetools")
                    , av = function() {
                    az.pvalueThreshold = az.pValueSlider.getSliderValue();
                    q("phenodcc_heatmap_pvalue_threshold", az.pvalueThreshold);
                    az.updatePvalueSections()
                };
                az.showpValueGradient = az.createCheckbox(ay, "Show gradient", false, function() {
                    if (az.showpValueGradient.isSelected) {
                        az.pValueSlider.hideSlider()
                    } else {
                        az.pValueSlider.showSlider()
                    }
                    az.updatePvalueSections()
                });
                aw = parseFloat(aw);
                if (isNaN(aw) || aw < 0 || aw > 1) {
                    aw = J
                }
                az.pValueSlider = new V(ay,"p-value-slider","p-value threshold:",0,1,50,500,function() {
                        ah(av, 50, az)
                    }
                    ,aw,J);
                az.pvalueThreshold = az.pValueSlider.valueFromPosition();
                az.appendMeanings(ax);
                az.breadcrumsNode = aC;
                az.updateNavigationBar();
                return az.toolbar = aA
            },
            createInterface: function(au) {
                var av = this;
                av.prepareToolbar(au);
                av.prepareSingleGeneContent(au);
                return au
            },
            destroyInterface: function(au) {
                ag(au)
            },
            pvaluePrecision: function(av) {
                var au = this;
                return {
                    v: v(au.pvalueExtractor(av, "v")),
                    o: v(au.pvalueExtractor(av, "o")),
                    e: v(au.pvalueExtractor(av, "e")),
                    m: v(au.pvalueExtractor(av, "m")),
                    s: v(au.pvalueExtractor(av, "s")),
                    os: v(au.pvalueExtractor(av, "os")),
                    es: v(au.pvalueExtractor(av, "es")),
                    ms: v(au.pvalueExtractor(av, "ms"))
                }
            },
            rowColPvaluePrecision: function(aw, au) {
                var av = this;
                return av.dataPvalues[aw][au] = av.pvaluePrecision(av.dataPvalues[aw][au])
            },
            getPvalueGradientColour: function(ay, av) {
                var ax = this, au, aw = (ay - ax.dataMinValue) / ax.dataValueRange;
                au = av ? a(ax.highlightedSignificantColourChannels, ax.highlightedInsignificantColourChannels, aw) : a(ax.significantColourChannels, ax.insignificantColourChannels, aw);
                return N(au)
            },
            checkWhatFieldsAredifferent: function(aC) {
                var ax, aA = aC.length, aD, aw = 0, az = 0, au = 0, ay, aB, av;
                if (aA > 0) {
                    aD = aC[0];
                    ay = aD.a;
                    aB = aD.s;
                    av = aD.i;
                    for (ax = 1; ax < aA; ++ax) {
                        if (ay !== aC[ax].a) {
                            aw = 1;
                            break
                        }
                    }
                    for (ax = 1; ax < aA; ++ax) {
                        if (aB !== aC[ax].s) {
                            az = 1;
                            break
                        }
                    }
                    for (ax = 1; ax < aA; ++ax) {
                        if (av !== aC[ax].i) {
                            au = 1;
                            break
                        }
                    }
                }
                return {
                    allele: aw,
                    strain: az,
                    centre: au
                }
            },
            generateRowIdentifiers: function() {
                var aB = this, aF = aB.dataColumnHeaders, ay, aA = aF.length, aD, aG, aw, az, av, au, ax, aE, aC = 32;
                aB.title = aF[0].g;
                if (aA === 1) {
                    aB.dataColumnHeaders[0].u = aB.title;
                    return
                } else {
                    aD = aB.checkWhatFieldsAredifferent(aF);
                    for (ay = 0; ay < aA; ++ay) {
                        aG = aF[ay];
                        aw = aG.a;
                        av = aG.s;
                        au = aG.c;
                        ax = aG.i;
                        aE = aw ? aw.match(/.*<sup>(.*)<\/sup>/) : null;
                        aB.dataColumnHeaders[ay].a = aw = aE ? aE[1] : "";
                        az = aw ? aw.split("(")[0] : "";
                        if (aD.allele && !aD.strain && !aD.centre) {
                            if (aw.search("tm1a\\(") !== -1) {
                                aB.dataColumnHeaders[ay].u = "Conditional ready"
                            } else {
                                if (aw.search("tm1b\\(") !== -1) {
                                    aB.dataColumnHeaders[ay].u = "Cre-excised deletion"
                                } else {
                                    if (aw.search("tm1c\\(") !== -1) {
                                        aB.dataColumnHeaders[ay].u = "Conditional"
                                    } else {
                                        if (aw.search("tm1d\\(") !== -1) {
                                            aB.dataColumnHeaders[ay].u = "Deletion"
                                        } else {
                                            if (aw.search("tm1e\\(") !== -1) {
                                                aB.dataColumnHeaders[ay].u = "Targeted non-conditional"
                                            } else {
                                                if (aw.search("tm1.1\\(") !== -1) {
                                                    aB.dataColumnHeaders[ay].u = "Cre-excised deletion"
                                                } else {
                                                    if (aw.search("tm1\\(") !== -1) {
                                                        aB.dataColumnHeaders[ay].u = "Deletion"
                                                    } else {
                                                        aB.dataColumnHeaders[ay].u = aw
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            if (!aD.allele && !aD.strain && aD.centre) {
                                aB.dataColumnHeaders[ay].u = au.length < aC ? au : ax
                            } else {
                                if (!aD.allele && aD.strain && !aD.centre) {
                                    aB.dataColumnHeaders[ay].u = av
                                } else {
                                    if (aD.allele && aD.centre) {
                                        aB.dataColumnHeaders[ay].u = az + " (" + ax + ")"
                                    } else {
                                        if (aD.allele && aD.strain) {
                                            aB.dataColumnHeaders[ay].u = az + " (" + av + ")"
                                        } else {
                                            if (aD.strain && aD.centre) {
                                                aB.dataColumnHeaders[ay].u = av + " (" + ax + ")"
                                            } else {
                                                aB.dataColumnHeaders[ay].u = ay + 1
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (aB.dataColumnHeaders[ay].u.length > aC) {
                            aB.dataColumnHeaders[ay].u = ay + 1
                        }
                    }
                }
            },
            processData: function() {
                var az = this, ax, av, ay, aB, aA, aw, au;
                az.pegged = {};
                ay = az.dataNumRows = az.dataRowHeaders.length,
                    aB = az.dataNumColumns = az.dataColumnHeaders.length;
                aw = 2;
                au = -2;
                for (ax = 0; ax < ay; ++ax) {
                    for (av = 0; av < aB; ++av) {
                        aA = az.dataPvalues[ax][av].v;
                        if (aA < 0) {
                            continue
                        }
                        if (aA < aw) {
                            aw = aA
                        } else {
                            if (aA > au) {
                                au = aA
                            }
                        }
                    }
                }
                az.dataMinValue = aw;
                az.dataMaxValue = au;
                az.dataValueRange = az.dataMaxValue - az.dataMinValue;
                az.generateRowIdentifiers()
            },
            getDataObject: function(av, au) {
                var aw = null;
                if (typeof av[au] === "undefined") {
                    K("Missing 'row_headers' object/attribute in data")
                } else {
                    aw = av[au]
                }
                return aw
            },
            extractHeatmapData: function(ax) {
                var aw = this, au, av, ay;
                if (typeof ax.heatmap === "undefined") {
                    K("Missing 'heatmap' object in data")
                } else {
                    ax = ax.heatmap;
                    au = aw.getDataObject(ax, "row_headers");
                    if (au) {
                        av = aw.getDataObject(ax, "column_headers");
                        if (av) {
                            ay = aw.getDataObject(ax, "significance");
                            if (ay) {
                                aw.data = ax;
                                aw.dataRowHeaders = au;
                                aw.dataColumnHeaders = av;
                                aw.dataPvalues = ay;
                                return true
                            }
                        }
                    }
                }
                return false
            },
            retrieveDataIfValid: function(av) {
                var au = this, aw;
                if (av === undefined) {
                    K("Server returned invalid data")
                } else {
                    aw = au.getDataObject(av, "success");
                    if (aw !== null) {
                        if (aw === true) {
                            return au.extractHeatmapData(av)
                        } else {
                            au.showErrorNotification(au.root, "Heatmap data unavailable.");
                            K("Failed to retrieve data at server")
                        }
                    }
                }
                return false
            },
            fillGeneDetails: function() {
                var az = this, aA = az.geneDetails, aC, aB, ay, aw, av = az.dataColumnHeaders, au, ax = av.length;
                ag(aA);
                aC = n(aA, "table");
                aB = n(aC, "thead");
                ay = n(aB, "tr");
                n(ay, "th", null, null, "#");
                n(ay, "th", null, null, "Descriptor");
                n(ay, "th", null, null, "Gene symbol");
                n(ay, "th", null, null, "Background strain");
                n(ay, "th", null, null, "Allele");
                n(ay, "th", null, null, "Phenotyping center");
                aB = n(aC, "tbody");
                for (au = 0; au < ax; ++au) {
                    aw = av[au];
                    ay = n(aB, "tr", null, "dcc-row-" + (au % 2 ? "even" : "odd"));
                    n(ay, "td", null, null, au + 1);
                    n(ay, "td", null, null, aw.u);
                    n(ay, "td", null, null, aw.g);
                    n(ay, "td", null, null, aw.s);
                    n(ay, "td", null, null, aw.a);
                    n(ay, "td", null, null, aw.c)
                }
            },
            showLoadingNotification: function(au, av) {
                ag(au);
                n(au, "div", null, "dcc-heatmap-loading", "<div></div><span>" + av + "</span>")
            },
            showErrorNotification: function(au, av) {
                ag(au);
                n(au, "div", null, "dcc-heatmap-error", "<div></div><span>" + av + "</span>")
            },
            hideLoadingNotification: function(au) {
                ag(au)
            },
            retrieveSingleGeneData: function(av, au) {
                var aw = this;
                if (aw.mgiId === undefined) {
                    K("MGI identifier must be defined")
                } else {
                    aw.showLoadingNotification(aw.content, "Loading heatmap...");
                    m(aw.heatmapUrl + "?" + (aw.mgiId === undefined ? "" : "mgiid=" + aw.mgiId) + (aw.type === undefined ? "" : "&type=" + aw.type), function(ax) {
                        if (aw.retrieveDataIfValid(ax)) {
                            aw.hideLoadingNotification(aw.content);
                            aw.processData();
                            aw.fillGeneDetails();
                            aw.renderPvalueGrid();
                            aw.pushBreadcrum(av, au);
                            q("phenodcc_heatmap_key", av);
                            q("phenodcc_heatmap_label", au)
                        }
                    })
                }
            },
            updatePvalueSections: function() {
                var ax = this, aw = ax.pvalueSections, av, aA, ay, az, au = "dcc-heatmap-nodata";
                if (aw === undefined) {
                    return
                }
                if (ax.showpValueGradient.isSelected) {
                    for (av = 0,
                             aA = aw.length; av < aA; ++av) {
                        az = aw[av];
                        ay = az.pvalue;
                        az = az.node;
                        ax.addIconsForPvalues(az, ay);
                        if (ay === undefined || ay.v === undefined || ay.s === undefined) {
                            am(az, au)
                        } else {
                            i(az, ax.getPvalueGradientColour(ay.v))
                        }
                    }
                } else {
                    for (av = 0,
                             aA = aw.length; av < aA; ++av) {
                        az = aw[av];
                        ay = az.pvalue;
                        az = az.node;
                        ax.addIconsForPvalues(az, ay);
                        if (ay === undefined || ay.v === undefined || ay.s === undefined) {
                            am(az, au)
                        } else {
                            i(az, ay.v < ax.pvalueThreshold ? ax.significantColour : ax.insignificantColour)
                        }
                    }
                }
            },
            getControlSetting: function(ax) {
                var aw = this
                    , au = b
                    , av = 0;
                if (ax !== undefined) {
                    if (ax.o < aw.pvalueThreshold) {
                        au |= ac;
                        ++av
                    }
                    if (ax.e < aw.pvalueThreshold) {
                        au |= an;
                        ++av
                    }
                    if (ax.m < aw.pvalueThreshold) {
                        au |= ar;
                        ++av
                    }
                }
                if (av !== 1) {
                    au |= A
                }
                return au
            },
            getPopupRowOnClickHandler: function(au, aw) {
                var av = this;
                return function(ax) {
                    ax = g(ax);
                    ai(ax);
                    window.open(av.viz(av.popupGid, au.k, av.getControlSetting(au.s), av.pvalueThreshold))
                }
            },
            getPopupToolbarOnClickHandler: function(au) {
                var av = this;
                return function(aw) {
                    aw = g(aw);
                    ai(aw);
                    window.open(av.viz(av.popupGid, au, av.getControlSetting(), av.pvalueThreshold))
                }
            },
            preparePhenotypeTable: function(aC, ay, aB) {
                var aF = this, aD = ay.length, az, aE, av, aH, aG, au, aI = "dcc-heatmap-popup-", aJ = n(aC, "div", null, aI + "details"), ax = n(n(aJ, "table"), "tbody"), aA = function(aM, aK, aO, aL, aN) {
                    if (aO !== undefined && aO !== -1) {
                        n(aM, "div", null, aK + "pvalue-label", aL + ":");
                        n(aM, "div", null, aK + "pvalue-" + (aO < aF.pvalueThreshold ? "hit" : "nohit"));
                        if (aN !== undefined && aN !== -1) {
                            n(aM, "div", null, aK + "sex-pvalue-" + (aN < aF.pvalueThreshold ? "hit" : "nohit"))
                        }
                        n(aM, "div", null, aK + "pvalue-value", (aL === "Sex" ? "" : aO))
                    }
                }, aw = function(aM, aK) {
                    var aP = n(aM, "td", null, aI + "i"), aL, aO = aK.s, aN = (aO.o < aF.pvalueThreshold || aO.e < aF.pvalueThreshold || aO.m < aF.pvalueThreshold);
                    if (aN) {
                        aL = n(aP, "div");
                        n(aM, "td", null, aI + "t", aK.t)
                    } else {
                        n(aM, "td", null, aI + "t-light", "n/a")
                    }
                    if (aN) {
                        au = L(aK.o);
                        if (au !== null) {
                            am(aL, au)
                        }
                    }
                };
                if (aB) {
                    aI += "ont-"
                }
                for (az = 0; az < aD; ++az) {
                    ay[az].s = aF.pvaluePrecision(ay[az].s);
                    aH = ay[az];
                    aE = n(ax, "tr", null, aI + (az % 2 ? "even" : "odd") + " clickable");
                    aE.onclick = aF.getPopupRowOnClickHandler(aH, aB);
                    if (aB) {
                        n(aE, "td", null, aI + "p", aH.a)
                    }
                    n(aE, "td", null, aI + "q", aH.n);
                    aw(aE, aH);
                    av = n(aE, "td", null, aI + "v");
                    if (aH.s !== undefined) {
                        aA(av, aI, aH.s.o, "Hom", aH.s.os);
                        aA(av, aI, aH.s.e, "Het", aH.s.es);
                        aA(av, aI, aH.s.m, "Hem", aH.s.ms)
                    }
                }
                return aJ
            },
            preparePopupContent: function(ax) {
                var aw = this, av = document.createDocumentFragment(), au;
                if (typeof ax.details === undefined) {
                    n(av, "div", null, "dcc-heatmap-popup-warning", "Server returned invalid details")
                } else {
                    ax = ax.details;
                    if (ax.length > 0) {
                        au = ax[0].a !== undefined;
                        al(aw, av, ax);
                        e(av, au);
                        av.popupContent = aw.preparePhenotypeTable(av, ax, au)
                    } else {
                        n(av, "div", null, "dcc-heatmap-popup-info", "No significant parameters under current p-value threshold")
                    }
                }
                return av
            },
            getCellOnMouseoverHandler: function(av, au) {
                var aw = this;
                return function(ay) {
                    ay = g(ay);
                    ai(ay);
                    if (aw.popupGid === av && aw.popupType === au) {
                        return
                    } else {
                        aw.popupGid = av;
                        aw.popupType = au
                    }
                    if (ay.touches) {
                        ay = ay.touches;
                        if (ay.length !== 1) {
                            return
                        }
                        ay = ay[0]
                    }
                    var ax = Y(ay)
                        , az = W(ay);
                    aw.showPopup(ax, az);
                    aw.updatePopupContent();
                    m(aw.detailsUrl + (!aw.isOntological && isNaN(parseInt(au)) ? "/parameter" : "") + (av === undefined ? "" : "?gid=" + av) + "&type=" + au + "&threshold=" + aw.pvalueThreshold, function(aA) {
                        if (aw.popupGid === av && aw.popupType === au) {
                            aw.updatePopupContent(aw.preparePopupContent(aA));
                            aw.movePopup(ax, az)
                        }
                    })
                }
            },
            getCellOnMousemoveHandler: function() {
                var au = this;
                return function(av) {
                    av = g(av);
                    ai(av);
                    au.movePopup(av)
                }
            },
            getCellOnClickHandler: function(aw, au, ax) {
                var av = this;
                return function(ay) {
                    ay = g(ay);
                    ai(ay);
                    window.open(av.viz(aw, au, av.getControlSetting(ax), av.pvalueThreshold))
                }
            },
            addIconsForPvalues: function(au, aw) {
                if (au) {
                    ag(au)
                }
                var av = this
                    , ax = function(ay, az, aA) {
                    return "pvalue-icon-" + ay + (aA < av.pvalueThreshold && az < av.pvalueThreshold ? "-hit" : "-nohit")
                };
                if (aw.o !== undefined) {
                    n(au, "div", null, ax("hom", aw.o, aw.o), "Hom")
                }
                if (aw.e !== undefined) {
                    n(au, "div", null, ax("het", aw.o, aw.e), "Het")
                }
                if (aw.m !== undefined) {
                    n(au, "div", null, ax("hem", aw.o, aw.m), "Hem")
                }
                if (aw.s !== undefined) {
                    n(au, "div", null, ax("sex", aw.o, aw.s), "")
                }
            },
            fillSection: function(aF, aw, ay, aB, aH, aN) {
                var aO = this, au = document.createDocumentFragment(), aC = n(au, "table", null, "dcc-heatmap-section"), aL = n(n(aC, "thead"), "tr", null, "dcc-heatmap-section-column-headers"), aG = n(aC, "tbody"), aA, ax, az, aM, aJ, aI, aE, av, aD, aK = aO.pvalueSections;
                n(aL, "th");
                for (aM = 0,
                         aJ = ay; aM < aB; ++aM,
                         ++aJ) {
                    az = aw[aJ];
                    aD = n(aL, "th", null, null, aO.rowFormatter(az));
                    ax = parseInt(aO.keyExtractor(az));
                    if (isNaN(ax)) {
                        if (aO.isOntological) {
                            aD.onmouseup = aO.getHeatmapRetriever(aO.keyExtractor(az), aD.innerHTML)
                        }
                        az.type = aO.keyExtractor(az)
                    } else {
                        aD.onmouseup = aO.getHeatmapRetriever(ax, aD.innerHTML);
                        az.type = ax
                    }
                    am(aD, "clickable")
                }
                for (aM = 0; aM < aH; ++aM) {
                    n(aL, "th")
                }
                if (aK === undefined) {
                    aK = []
                }
                for (aM = 0; aM < aN; ++aM) {
                    av = n(aG, "tr");
                    aA = aO.columnFormatter(aF[aM]);
                    n(av, "td", null, "dcc-heatmap-section-row-headers", aA);
                    for (aJ = 0,
                             aI = ay; aJ < aB; ++aJ,
                             ++aI) {
                        az = aw[aI];
                        aE = aO.rowColPvaluePrecision(aI, aM);
                        aD = n(av, "td");
                        aK.push({
                            node: aD,
                            pvalue: aE
                        });
                        aO.addCellEventHandlers(aD, aE, aO.keyExtractor(aF[aM]), az.type)
                    }
                }
                aO.pvalueSections = aK;
                aO.updatePvalueSections();
                aO.content.appendChild(au);
                aO.content.onmouseover = function(aP) {
                    aP = g(aP);
                    ai(aP);
                    aO.hidePopup(aP)
                }
            },
            renderPvalueGrid: function() {
                var az = this, aB = 0, ay, aw = az.data.row_headers, aA = az.data.column_headers, ax = aw.length, au = aA.length, av = az.numColumns;
                if (ax < 1) {
                    n(az.content, "div", null, "dcc-heatmap-no-sub", "No subcategory found")
                } else {
                    while (ax > 0) {
                        ay = av < ax ? av : ax;
                        ax -= av;
                        az.fillSection(aA, aw, aB, ay, av - ay, au);
                        aB += ay
                    }
                }
            },
            viewSingleGene: function() {
                var aw = this, av = l("phenodcc_heatmap_key"), au = l("phenodcc_heatmap_label"), ax;
                aw.createInterface(aw.root);
                aw.prepareColours();
                if (av && au) {
                    ax = aw.getHeatmapRetriever(av, au);
                    ax()
                } else {
                    aw.retrieveSingleGeneData()
                }
                aw.createColourPicker(aw.root);
                aw.createPopup(aw.root)
            },
            createPopup: function(av) {
                var ax = this
                    , ay = "dcc-heatmap-popup"
                    , au = n(av, "div", null, ay)
                    , aw = n(au, "div", null, ay + "-content")
                    , az = n(au, "div", null, ay + "-close");
                w(au, "mouseover", function(aA) {
                    aA = g(aA);
                    ai(aA)
                });
                w(az, "click", function(aA) {
                    aA = g(aA);
                    ax.hidePopup(aA)
                });
                au.popupContent = aw;
                au.popupClose = az;
                return ax.popup = au
            },
            updatePopupContent: function(aw) {
                var av = this
                    , au = av.popup.popupContent;
                if (aw === undefined) {
                    av.showLoadingNotification(au, "Loading details...")
                } else {
                    ag(au);
                    au.appendChild(aw)
                }
            },
            showPopup: function(au, ay, ax) {
                var aw = this
                    , av = aw.popup;
                aw.updatePopupContent(ax);
                E(av, "visibility", "visible");
                aw.movePopup(au, ay)
            },
            popupHasScroll: function() {
                var av = this
                    , au = av.popup.popupContent;
                return au === undefined ? false : ak(au) > aq(au)
            },
            getPopupPosition: function(aD, aA) {
                console.log(this);
                console.log(aD);
                console.log(aA);
                var az = this, aB = 50, aw = az.popup, aE, aC = S(aw), ax = dcc.ie8 ? 0 : aq(aw), aF = aD + aC + aB, av = aA + ax + aB, ay = t(), au = p();
                aE = aF - ay;
                if (aE > 0) {
                    aD -= aE
                }
                aE = av - au;
                if (aE > 0) {
                    aA -= aE
                }
                return {
                    x: aD,
                    y: aA
                }
            },
            movePopup: function(au, ay) {
                var aw = this
                    , av = aw.popup
                    , ax = aw.getPopupPosition(au, ay);
                u(av, ax.x);
                B(av, ax.y)
            },
            hidePopup: function() {
                var au = this;
                E(au.popup, "visibility", "hidden");
                au.popupGid = au.popupType = null
            },
            checkHostDivElement: function(aw) {
                var av = this
                    , au = document.getElementById(aw);
                if (au === null) {
                    alert("Invalid heatmap host:\nNo DOM node with identifier'" + aw + "'");
                    return false
                }
                av.parent = au;
                return true
            },
            initialiseHeatmap: function() {
                var av = this
                    , au = av.parent;
                av.updateUrls();
                am(au, "phenodcc-heatmap");
                av.geneDetails = n(au, "div", null, "dcc-heatmap-genes");
                av.embryo = n(au, "div", "dcc-heatmap-embryo");
                new dcc.EmbryoHeatmap({
                    id: "dcc-heatmap-embryo",
                    mgiid: av.mgiId,
                    rest: av.json
                });
                av.root = n(au, "div", null, "dcc-heatmap-root");
                w(av.root, "touchstart", function(aw) {
                    if (aw.touches) {
                        aw = aw.touches;
                        if (aw.length === 2) {
                            av.hidePopup();
                            av.hideColourPicker()
                        }
                    }
                });
                w(T, "mouseover", function(aw) {
                    av.hidePopup();
                    av.hideColourPicker()
                });
                av.viewSingleGene();
                return true
            }
        };
        dcc.EmbryoHeatmap = function(au) {
            this.id = au.id;
            this.parent = document.getElementById(this.id);
            this.mgiid = au.mgiid;
            this.url = au.rest + "embryo/summary/";
            this.init()
        }
        ;
        dcc.EmbryoHeatmap.prototype = {
            init: function() {
                var av = this, au;
                m(av.url + av.mgiid, function(aw) {
                    if (aw === undefined || aw.length === 0) {
                        return
                    }
                    au = av.process(aw);
                    av.render(au)
                })
            },
            process: function(ax) {
                var aw = this, av, au, az, ay;
                aw.gids = {};
                aw.tabLabels = [];
                for (av in ax) {
                    au = ax[av];
                    if (aw.gids[au.gid] === undefined) {
                        aw.gids[au.gid] = [];
                        ay = /.*<sup>(.*)<\/sup>/.exec(au.allele);
                        aw.tabLabels[au.gid] = {
                            c: au.centre,
                            a: au.allele,
                            x: ay === null ? au.allele : ay[1]
                        }
                    }
                    az = aw.gids[au.gid];
                    if (az[au.num] === undefined) {
                        az[au.num] = []
                    }
                    ay = az[au.num];
                    if (ay[au.procedureName] === undefined) {
                        ay[au.procedureName] = []
                    }
                    ay = ay[au.procedureName];
                    if (ay[au.parameterKey] === undefined) {
                        ay[au.parameterKey] = au
                    }
                }
                return au.gid
            },
            getPhenoviewHandler: function(aw, av) {
                var au = this;
                return function() {
                    window.open("/phenoview/?gid=" + aw.gid + "-" + aw.sid + "-" + aw.cid + "&qeid=" + (av === undefined ? aw.parameterKey : av))
                }
            },
            getEmbryoViewerHandler: function(av) {
                var au = this;
                return function() {
                    window.open("/embryoviewer?mgi=" + au.mgiid + "&pid=" + av.procedureId)
                }
            },
            renderStageEntry: function(au, ay) {
                var ax = this, aw, av, az = undefined;
                aw = n(au, "div", null, "dcc-embryo-stage-entry");
                av = n(aw, "div", null, "parameter-name", ay.parameterName);
                switch (ay.graphType) {
                    case "IMAGE":
                        az = "image";
                        break;
                    case "CATEGORICAL":
                        az = "categorical";
                        break;
                    case "1D":
                    case "2D":
                        az = "numerical";
                        break
                }
                if (ay.parameterName === "Embryo reconstruction") {
                    az = "image"
                }
                if (az) {
                    av.setAttribute("class", av.getAttribute("class") + " dcc-" + az + "-parameter")
                }
                aw.onclick = ay.parameterName === "Embryo reconstruction" ? ax.getEmbryoViewerHandler(ay) : ax.getPhenoviewHandler(ay)
            },
            getTabSelector: function(au) {
                var av = this;
                return function() {
                    av.render(au)
                }
            },
            render: function(aA) {
                var aL = this, au, aF, aE, aJ, aC, aB, aG, aI, aw = "", aH, aM, aD, az, aK, ay, ax, av = /([E0-9.-]+).*/;
                ag(aL.parent);
                aD = n(aL.parent, "div", null, "dcc-embryo-selector");
                aI = n(aL.parent, "table", null, "dcc-embryo-stages");
                au = aL.gids[aA];
                for (aF in au) {
                    aE = au[aF];
                    aG = n(aI, "tr");
                    ax = aF.match(av);
                    n(aG, "td", null, null, ax.length > 1 ? ax[1] : aF);
                    aG = n(aG, "td");
                    for (aC in aE) {
                        aM = n(aG, "div", null, "dcc-embryo-stage-procs-title");
                        az = n(aG, "div", null, "dcc-embryo-stage-procs");
                        aJ = aE[aC];
                        aw = "";
                        aK = "";
                        for (aB in aJ) {
                            aH = aJ[aB];
                            if (aH.parameterKey && (aH.parameterKey.indexOf("_EVL_") !== -1 || aH.parameterKey.indexOf("_EVM_") !== -1 || aH.parameterKey.indexOf("_EVO_") !== -1 || aH.parameterKey.indexOf("_EVP_") !== -1)) {
                                if (aH.parameterName === "Outcome") {
                                    aK = aH.value;
                                    aw += aH.parameterKey + ","
                                }
                            } else {
                                aL.renderStageEntry(n(az, "div", null, "dcc-embryo-stage-params"), aH);
                                aw += aH.parameterKey + ","
                            }
                        }
                        n(aM, "div", null, null, aC);
                        if (aK !== "") {
                            ay = n(az, "div", null, "dcc-viability-proc-outcome", aK);
                            ay.onclick = aL.getPhenoviewHandler(aH);
                            aM.setAttribute("class", aM.getAttribute("class") + " dcc-viability-proc-title")
                        }
                        aM.onclick = aL.getPhenoviewHandler(aH, aw)
                    }
                }
                n(aD, "div", null, "dcc-embryo-selector-gene-symbol", aH.allele.split("<")[0] + " : Embryo");
                aD = n(aD, "div", null, "dcc-embryo-selector-tabs");
                for (aF in aL.gids) {
                    aB = aF === aA + "" ? " active" : "";
                    aC = aL.tabLabels[aF];
                    aB = n(aD, "div", null, "dcc-embryo-selector-centre" + aB);
                    n(aB, "div", null, null, aC.c);
                    n(aB, "div", null, null, aC.x);
                    aB.onclick = aL.getTabSelector(aF)
                }
            }
        }
    }
)();
