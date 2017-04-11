'use strict';

angular.module('reachoutApp', ['LocalStorageModule', 'tmh.dynamicLocale', 'pascalprecht.translate', 
    'ngResource', 'ngCookies', 'ngAria', 'ngCacheBuster', 'ngFileUpload',
    // jhipster-needle-angularjs-add-module JHipster will add new module
    'ui.bootstrap', 'ui.router',  'infinite-scroll', 'angular-loading-bar'])

    .run(["$rootScope", "$location", "$window", "$http", "$state", "$translate", "Language", "Auth", "Principal", "ENV", "VERSION", function ($rootScope, $location, $window, $http, $state, $translate, Language, Auth, Principal, ENV, VERSION) {
        // update the window title using params in the following
        // precendence
        // 1. titleKey parameter
        // 2. $state.$current.data.pageTitle (current state page title)
        // 3. 'global.title'
        var updateTitle = function(titleKey) {
            if (!titleKey && $state.$current.data && $state.$current.data.pageTitle) {
                titleKey = $state.$current.data.pageTitle;
            }
            $translate(titleKey || 'global.title').then(function (title) {
                $window.document.title = title;
            });
        };
        
        $rootScope.ENV = ENV;
        $rootScope.VERSION = VERSION;
        $rootScope.$on('$stateChangeStart', function (event, toState, toStateParams) {
            $rootScope.toState = toState;
            $rootScope.toStateParams = toStateParams;

            if (Principal.isIdentityResolved()) {
                Auth.authorize();
            }
            
            // Update the language
            Language.getCurrent().then(function (language) {
                $translate.use(language);
            });
            
        });

        $rootScope.$on('$stateChangeSuccess',  function(event, toState, toParams, fromState, fromParams) {
            var titleKey = 'global.title' ;

            // Remember previous state unless we've been redirected to login or we've just
            // reset the state memory after logout. If we're redirected to login, our
            // previousState is already set in the authExpiredInterceptor. If we're going
            // to login directly, we don't want to be sent to some previous state anyway
            if (toState.name != 'login' && $rootScope.previousStateName) {
              $rootScope.previousStateName = fromState.name;
              $rootScope.previousStateParams = fromParams;
            }

            // Set the page title key to the one configured in state or use default one
            if (toState.data.pageTitle) {
                titleKey = toState.data.pageTitle;
            }
            updateTitle(titleKey);
        });
        
        // if the current translation changes, update the window title
        $rootScope.$on('$translateChangeSuccess', function() { updateTitle(); });

        
        $rootScope.back = function() {
            // If previous state is 'activate' or do not exist go to 'home'
            if ($rootScope.previousStateName === 'activate' || $state.get($rootScope.previousStateName) === null) {
                $state.go('home');
            } else {
                $state.go($rootScope.previousStateName, $rootScope.previousStateParams);
            }
        };
    }])
    .config(["$stateProvider", "$urlRouterProvider", "$httpProvider", "$locationProvider", "$translateProvider", "tmhDynamicLocaleProvider", "httpRequestInterceptorCacheBusterProvider", "AlertServiceProvider", function ($stateProvider, $urlRouterProvider, $httpProvider, $locationProvider, $translateProvider, tmhDynamicLocaleProvider, httpRequestInterceptorCacheBusterProvider, AlertServiceProvider) {
        // uncomment below to make alerts look like toast
        //AlertServiceProvider.showAsToast(true);

        //Cache everything except rest api requests
        httpRequestInterceptorCacheBusterProvider.setMatchlist([/.*api.*/, /.*protected.*/], true);

        $urlRouterProvider.otherwise('/');
        $stateProvider.state('site', {
            'abstract': true,
            views: {
                'navbar@': {
                    templateUrl: 'scripts/components/navbar/navbar.html',
                    controller: 'NavbarController'
                }
            },
            resolve: {
                authorize: ['Auth',
                    function (Auth) {
                        return Auth.authorize();
                    }
                ],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('global');
                }]
            }
        });

        $httpProvider.interceptors.push('errorHandlerInterceptor');
        $httpProvider.interceptors.push('authExpiredInterceptor');
        $httpProvider.interceptors.push('authInterceptor');
        $httpProvider.interceptors.push('notificationInterceptor');
        
        // Initialize angular-translate
        $translateProvider.useLoader('$translatePartialLoader', {
            urlTemplate: 'i18n/{lang}/{part}.json'
        });

        $translateProvider.preferredLanguage('en');
        $translateProvider.useCookieStorage();
        $translateProvider.useSanitizeValueStrategy('escaped');
        $translateProvider.addInterpolation('$translateMessageFormatInterpolation');

        tmhDynamicLocaleProvider.localeLocationPattern('bower_components/angular-i18n/angular-locale_{{locale}}.js');
        tmhDynamicLocaleProvider.useCookieStorage();
        tmhDynamicLocaleProvider.storageKey('NG_TRANSLATE_LANG_KEY');
        
    }])
    // jhipster-needle-angularjs-add-config JHipster will add new application configuration
    .config(['$urlMatcherFactoryProvider', function($urlMatcherFactory) {
        $urlMatcherFactory.type('boolean', {
            name : 'boolean',
            decode: function(val) { return val == true ? true : val == "true" ? true : false },
            encode: function(val) { return val ? 1 : 0; },
            equals: function(a, b) { return this.is(a) && a === b; },
            is: function(val) { return [true,false,0,1].indexOf(val) >= 0 },
            pattern: /bool|true|0|1/
        });
    }]);

"use strict";
// DO NOT EDIT THIS FILE, EDIT THE GRUNT TASK NGCONSTANT SETTINGS INSTEAD WHICH GENERATES THIS FILE
angular.module('reachoutApp')

.constant('ENV', 'prod')

.constant('VERSION', '0.0.1-SNAPSHOT')

;
"use strict";
// DO NOT EDIT THIS FILE, EDIT THE GRUNT TASK NGCONSTANT SETTINGS INSTEAD WHICH GENERATES THIS FILE
angular.module('reachoutApp')

.constant('ENV', 'dev')

.constant('VERSION', '0.0.1-SNAPSHOT')

;
'use strict';

angular.module('reachoutApp')
    .factory('Auth', ["$rootScope", "$state", "$q", "$translate", "Principal", "AuthServerProvider", "Account", "Register", "Activate", "Password", "PasswordResetInit", "PasswordResetFinish", "Tracker", function Auth($rootScope, $state, $q, $translate, Principal, AuthServerProvider, Account, Register, Activate, Password, PasswordResetInit, PasswordResetFinish, Tracker) {
        return {
            login: function (credentials, callback) {
                var cb = callback || angular.noop;
                var deferred = $q.defer();

                AuthServerProvider.login(credentials).then(function (data) {
                    // retrieve the logged account information
                    Principal.identity(true).then(function(account) {
                        // After the login the language will be changed to
                        // the language selected by the user during his registration
                        $translate.use(account.langKey).then(function(){
                            $translate.refresh();
                        });
                        Tracker.sendActivity();
                        deferred.resolve(data);
                    });
                    return cb();
                }).catch(function (err) {
                    this.logout();
                    deferred.reject(err);
                    return cb(err);
                }.bind(this));

                return deferred.promise;
            },

            logout: function () {
                AuthServerProvider.logout();
                Principal.authenticate(null);
                // Reset state memory
                $rootScope.previousStateName = undefined;
                $rootScope.previousStateNameParams = undefined;
            },

            authorize: function(force) {
                return Principal.identity(force)
                    .then(function() {
                        var isAuthenticated = Principal.isAuthenticated();

                        // an authenticated user can't access to login and register pages
                        if (isAuthenticated && $rootScope.toState.parent === 'account' && ($rootScope.toState.name === 'login' || $rootScope.toState.name === 'register')) {
                            $state.go('home');
                        }

                        if ($rootScope.toState.data.authorities && $rootScope.toState.data.authorities.length > 0 && !Principal.hasAnyAuthority($rootScope.toState.data.authorities)) {
                            if (isAuthenticated) {
                                // user is signed in but not authorized for desired state
                                $state.go('accessdenied');
                            }
                            else {
                                // user is not authenticated. stow the state they wanted before you
                                // send them to the signin state, so you can return them when you're done
                                $rootScope.previousStateName = $rootScope.toState;
                                $rootScope.previousStateNameParams = $rootScope.toStateParams;

                                // now, send them to the signin state so they can log in
                                $state.go('login');
                            }
                        }
                    });
            },
            createAccount: function (account, callback) {
                var cb = callback || angular.noop;

                return Register.save(account,
                    function () {
                        return cb(account);
                    },
                    function (err) {
                        this.logout();
                        return cb(err);
                    }.bind(this)).$promise;
            },

            updateAccount: function (account, callback) {
                var cb = callback || angular.noop;

                return Account.save(account,
                    function () {
                        return cb(account);
                    },
                    function (err) {
                        return cb(err);
                    }.bind(this)).$promise;
            },

            activateAccount: function (key, callback) {
                var cb = callback || angular.noop;

                return Activate.get(key,
                    function (response) {
                        return cb(response);
                    },
                    function (err) {
                        return cb(err);
                    }.bind(this)).$promise;
            },

            changePassword: function (newPassword, callback) {
                var cb = callback || angular.noop;

                return Password.save(newPassword, function () {
                    return cb();
                }, function (err) {
                    return cb(err);
                }).$promise;
            },

            resetPasswordInit: function (mail, callback) {
                var cb = callback || angular.noop;

                return PasswordResetInit.save(mail, function() {
                    return cb();
                }, function (err) {
                    return cb(err);
                }).$promise;
            },

            resetPasswordFinish: function(keyAndPassword, callback) {
                var cb = callback || angular.noop;

                return PasswordResetFinish.save(keyAndPassword, function () {
                    return cb();
                }, function (err) {
                    return cb(err);
                }).$promise;
            }
        };
    }]);

'use strict';

angular.module('reachoutApp')
    .factory('Principal', ["$q", "Account", "Tracker", function Principal($q, Account, Tracker) {
        var _identity,
            _authenticated = false;

        return {
            isIdentityResolved: function () {
                return angular.isDefined(_identity);
            },
            isAuthenticated: function () {
                return _authenticated;
            },
            hasAuthority: function (authority) {
                if (!_authenticated) {
                    return $q.when(false);
                }

                return this.identity().then(function(_id) {
                    return _id.authorities && _id.authorities.indexOf(authority) !== -1;
                }, function(err){
                    return false;
                });
            },
            hasAnyAuthority: function (authorities) {
                if (!_authenticated || !_identity || !_identity.authorities) {
                    return false;
                }

                for (var i = 0; i < authorities.length; i++) {
                    if (_identity.authorities.indexOf(authorities[i]) !== -1) {
                        return true;
                    }
                }

                return false;
            },
            authenticate: function (identity) {
                _identity = identity;
                _authenticated = identity !== null;
            },
            identity: function (force) {
                var deferred = $q.defer();

                if (force === true) {
                    _identity = undefined;
                }

                // check and see if we have retrieved the identity data from the server.
                // if we have, reuse it by immediately resolving
                if (angular.isDefined(_identity)) {
                    deferred.resolve(_identity);

                    return deferred.promise;
                }

                // retrieve the identity data from the server, update the identity object, and then resolve.
                Account.get().$promise
                    .then(function (account) {
                        _identity = account.data;
                        _authenticated = true;
                        deferred.resolve(_identity);
                        Tracker.connect();
                    })
                    .catch(function() {
                        _identity = null;
                        _authenticated = false;
                        deferred.resolve(_identity);
                    });
                return deferred.promise;
            }
        };
    }]);

'use strict';

angular.module('reachoutApp')
    .directive('hasAnyAuthority', ['Principal', function (Principal) {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var setVisible = function () {
                        element.removeClass('hidden');
                    },
                    setHidden = function () {
                        element.addClass('hidden');
                    },
                    defineVisibility = function (reset) {
                        var result;
                        if (reset) {
                            setVisible();
                        }

                        result = Principal.hasAnyAuthority(authorities);
                        if (result) {
                            setVisible();
                        } else {
                            setHidden();
                        }
                    },
                    authorities = attrs.hasAnyAuthority.replace(/\s+/g, '').split(',');

                if (authorities.length > 0) {
                    defineVisibility(true);
                }
            }
        };
    }])
    .directive('hasAuthority', ['Principal', function (Principal) {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var setVisible = function () {
                        element.removeClass('hidden');
                    },
                    setHidden = function () {
                        element.addClass('hidden');
                    },
                    defineVisibility = function (reset) {

                        if (reset) {
                            setVisible();
                        }

                        Principal.hasAuthority(authority)
                            .then(function (result) {
                                if (result) {
                                    setVisible();
                                } else {
                                    setHidden();
                                }
                            });
                    },
                    authority = attrs.hasAuthority.replace(/\s+/g, '');

                if (authority.length > 0) {
                    defineVisibility(true);
                }
            }
        };
    }]);

'use strict';

angular.module('reachoutApp')
    .factory('Account', ["$resource", function Account($resource) {
        return $resource('api/account', {}, {
            'get': { method: 'GET', params: {}, isArray: false,
                interceptor: {
                    response: function(response) {
                        // expose response
                        return response;
                    }
                }
            }
        });
    }]);

'use strict';

angular.module('reachoutApp')
    .factory('Activate', ["$resource", function ($resource) {
        return $resource('api/activate', {}, {
            'get': { method: 'GET', params: {}, isArray: false}
        });
    }]);



'use strict';

angular.module('reachoutApp')
    .factory('Password', ["$resource", function ($resource) {
        return $resource('api/account/change_password', {}, {
        });
    }]);

angular.module('reachoutApp')
    .factory('PasswordResetInit', ["$resource", function ($resource) {
        return $resource('api/account/reset_password/init', {}, {
        })
    }]);

angular.module('reachoutApp')
    .factory('PasswordResetFinish', ["$resource", function ($resource) {
        return $resource('api/account/reset_password/finish', {}, {
        })
    }]);

'use strict';

angular.module('reachoutApp')
    .factory('Register', ["$resource", function ($resource) {
        return $resource('api/register', {}, {
        });
    }]);



/* globals $ */
'use strict';

angular.module('reachoutApp')
    .directive('showValidation', function() {
        return {
            restrict: 'A',
            require: 'form',
            link: function (scope, element) {
                element.find('.form-group').each(function() {
                    var $formGroup = $(this);
                    var $inputs = $formGroup.find('input[ng-model],textarea[ng-model],select[ng-model]');

                    if ($inputs.length > 0) {
                        $inputs.each(function() {
                            var $input = $(this);
                            scope.$watch(function() {
                                return $input.hasClass('ng-invalid') && $input.hasClass('ng-dirty');
                            }, function(isInvalid) {
                                $formGroup.toggleClass('has-error', isInvalid);
                            });
                        });
                    }
                });
            }
        };
    });

/* globals $ */
'use strict';

angular.module('reachoutApp')
    .directive('maxbytes', ["$q", function ($q) {
        function endsWith(suffix, str) {
            return str.indexOf(suffix, str.length - suffix.length) !== -1;
        }

        function paddingSize(base64String) {
            if (endsWith('==', base64String)) {
                return 2;
            }
            if (endsWith('=', base64String)) {
                return 1;
            }
            return 0;
        }

        function numberOfBytes(base64String) {
            return base64String.length / 4 * 3 - paddingSize(base64String);
        }

        return {
            restrict: 'A',
            require: '?ngModel',
            link: function (scope, element, attrs, ngModel) {
                if (!ngModel) return;

                ngModel.$validators.maxbytes = function (modelValue) {
                    return ngModel.$isEmpty(modelValue) || numberOfBytes(modelValue) <= attrs.maxbytes;
                };
            }
        };
    }]);

/* globals $ */
'use strict';

angular.module('reachoutApp')
    .directive('minbytes', ["$q", function ($q) {
        function endsWith(suffix, str) {
            return str.indexOf(suffix, str.length - suffix.length) !== -1;
        }

        function paddingSize(base64String) {
            if (endsWith('==', base64String)) {
                return 2;
            }
            if (endsWith('=', base64String)) {
                return 1;
            }
            return 0;
        }

        function numberOfBytes(base64String) {
            return base64String.length / 4 * 3 - paddingSize(base64String);
        }

        return {
            restrict: 'A',
            require: '?ngModel',
            link: function (scope, element, attrs, ngModel) {
                if (!ngModel) return;

                ngModel.$validators.minbytes = function (modelValue) {
                    return ngModel.$isEmpty(modelValue) || numberOfBytes(modelValue) >= attrs.minbytes;
                };
            }
        };
    }]);

'use strict';

angular.module('reachoutApp')
    .config(["uibPagerConfig", function (uibPagerConfig) {
        uibPagerConfig.itemsPerPage = 20;
        uibPagerConfig.previousText = '«';
        uibPagerConfig.nextText = '»';
    }]);

'use strict';

angular.module('reachoutApp')
    .config(["uibPaginationConfig", function (uibPaginationConfig) {
        uibPaginationConfig.itemsPerPage = 20;
        uibPaginationConfig.maxSize = 5;
        uibPaginationConfig.boundaryLinks = true;
        uibPaginationConfig.firstText = '«';
        uibPaginationConfig.previousText = '‹';
        uibPaginationConfig.nextText = '›';
        uibPaginationConfig.lastText = '»';
    }]);

'use strict';

angular.module('reachoutApp')
    .factory('AuditsService', ["$http", function ($http) {
        return {
            findAll: function () {
                return $http.get('api/audits/').then(function (response) {
                    return response.data;
                });
            },
            findByDates: function (fromDate, toDate) {

                var formatDate =  function (dateToFormat) {
                    if (dateToFormat !== undefined && !angular.isString(dateToFormat)) {
                        return dateToFormat.getYear() + '-' + dateToFormat.getMonth() + '-' + dateToFormat.getDay();
                    }
                    return dateToFormat;
                };

                return $http.get('api/audits/', {params: {fromDate: formatDate(fromDate), toDate: formatDate(toDate)}}).then(function (response) {
                    return response.data;
                });
            }
        };
    }]);

'use strict';

angular.module('reachoutApp')
    .factory('LogsService', ["$resource", function ($resource) {
        return $resource('api/logs', {}, {
            'findAll': { method: 'GET', isArray: true},
            'changeLevel': { method: 'PUT'}
        });
    }]);

'use strict';

angular.module('reachoutApp')
    .factory('ConfigurationService', ["$rootScope", "$filter", "$http", function ($rootScope, $filter, $http) {
        return {
            get: function() {
                return $http.get('configprops').then(function (response) {
                    var properties = [];
                    angular.forEach(response.data, function (data) {
                        properties.push(data);
                    });
                    var orderBy = $filter('orderBy');
                    return orderBy(properties, 'prefix');
                });
            }
        };
    }]);

'use strict';

angular.module('reachoutApp')
    .factory('MonitoringService', ["$rootScope", "$http", function ($rootScope, $http) {
        return {
            getMetrics: function () {
                return $http.get('metrics/metrics').then(function (response) {
                    return response.data;
                });
            },

            checkHealth: function () {
                return $http.get('health').then(function (response) {
                    return response.data;
                });
            },

            threadDump: function () {
                return $http.get('dump').then(function (response) {
                    return response.data;
                });
            }
        };
    }]);

'use strict';

angular.module('reachoutApp')
    .factory('authInterceptor', ["$rootScope", "$q", "$location", "localStorageService", function ($rootScope, $q, $location, localStorageService) {
        return {
            // Add authorization token to headers
            request: function (config) {
                config.headers = config.headers || {};
                var token = localStorageService.get('token');
                
                if (token && token.expires && token.expires > new Date().getTime()) {
                  config.headers['x-auth-token'] = token.token;
                }
                
                return config;
            }
        };
    }])
    .factory('authExpiredInterceptor', ["$rootScope", "$q", "$injector", "localStorageService", function ($rootScope, $q, $injector, localStorageService) {
        return {
            responseError: function (response) {
                // token has expired
                if (response.status === 401 && (response.data.error == 'invalid_token' || response.data.error == 'Unauthorized')) {
                    localStorageService.remove('token');
                    var Principal = $injector.get('Principal');
                    if (Principal.isAuthenticated()) {
                        var Auth = $injector.get('Auth');
                        Auth.authorize(true);
                    }
                }
                return $q.reject(response);
            }
        };
    }]);

'use strict';

angular.module('reachoutApp')
    .factory('errorHandlerInterceptor', ["$q", "$rootScope", function ($q, $rootScope) {
        return {
            'responseError': function (response) {
                if (!(response.status == 401 && response.data.path.indexOf("/api/account") == 0 )){
	                $rootScope.$emit('reachoutApp.httpError', response);
	            }
                return $q.reject(response);
            }
        };
    }]);
 'use strict';

angular.module('reachoutApp')
    .factory('notificationInterceptor', ["$q", "AlertService", function ($q, AlertService) {
        return {
            response: function(response) {
                var alertKey = response.headers('X-reachoutApp-alert');
                if (angular.isString(alertKey)) {
                    AlertService.success(alertKey, { param : response.headers('X-reachoutApp-params')});
                }
                return response;
            }
        };
    }]);

'use strict';

angular.module('reachoutApp')
    .directive('activeMenu', ["$translate", "$locale", "tmhDynamicLocale", function($translate, $locale, tmhDynamicLocale) {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var language = attrs.activeMenu;

                scope.$watch(function() {
                    return $translate.use();
                }, function(selectedLanguage) {
                    if (language === selectedLanguage) {
                        tmhDynamicLocale.set(language);
                        element.addClass('active');
                    } else {
                        element.removeClass('active');
                    }
                });
            }
        };
    }])
    .directive('activeLink', ["location", function(location) {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var clazz = attrs.activeLink;
                var path = attrs.href;
                path = path.substring(1); //hack because path does bot return including hashbang
                scope.location = location;
                scope.$watch('location.path()', function(newPath) {
                    if (path === newPath) {
                        element.addClass(clazz);
                    } else {
                        element.removeClass(clazz);
                    }
                });
            }
        };
    }]);

'use strict';

angular.module('reachoutApp')
    .controller('NavbarController', ["$scope", "$location", "$state", "Auth", "Principal", "ENV", function ($scope, $location, $state, Auth, Principal, ENV) {
        $scope.isAuthenticated = Principal.isAuthenticated;
        $scope.$state = $state;
        $scope.inProduction = ENV === 'prod';

        $scope.logout = function () {
            Auth.logout();
            $state.go('home');
        };
    }]);

'use strict';

angular.module('reachoutApp')
    .factory('User', ["$resource", function ($resource) {
        return $resource('api/users/:login', {}, {
                'query': {method: 'GET', isArray: true},
                'get': {
                    method: 'GET',
                    transformResponse: function (data) {
                        data = angular.fromJson(data);
                        return data;
                    }
                },
                'save': { method:'POST' },
                'update': { method:'PUT' },
                'delete':{ method:'DELETE'}
            });
        }]);

'use strict';

angular.module('reachoutApp')
    .filter('characters', function () {
        return function (input, chars, breakOnWord) {
            if (isNaN(chars)) {
                return input;
            }
            if (chars <= 0) {
                return '';
            }
            if (input && input.length > chars) {
                input = input.substring(0, chars);

                if (!breakOnWord) {
                    var lastspace = input.lastIndexOf(' ');
                    // Get last space
                    if (lastspace !== -1) {
                        input = input.substr(0, lastspace);
                    }
                } else {
                    while (input.charAt(input.length-1) === ' ') {
                        input = input.substr(0, input.length - 1);
                    }
                }
                return input + '...';
            }
            return input;
        };
    })
    .filter('words', function () {
        return function (input, words) {
            if (isNaN(words)) {
                return input;
            }
            if (words <= 0) {
                return '';
            }
            if (input) {
                var inputWords = input.split(/\s+/);
                if (inputWords.length > words) {
                    input = inputWords.slice(0, words).join(' ') + '...';
                }
            }
            return input;
        };
    });

/*jshint bitwise: false*/
'use strict';

angular.module('reachoutApp')
    .service('Base64', function () {
        var keyStr = 'ABCDEFGHIJKLMNOP' +
            'QRSTUVWXYZabcdef' +
            'ghijklmnopqrstuv' +
            'wxyz0123456789+/' +
            '=';
        this.encode = function (input) {
            var output = '',
                chr1, chr2, chr3 = '',
                enc1, enc2, enc3, enc4 = '',
                i = 0;

            while (i < input.length) {
                chr1 = input.charCodeAt(i++);
                chr2 = input.charCodeAt(i++);
                chr3 = input.charCodeAt(i++);

                enc1 = chr1 >> 2;
                enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
                enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
                enc4 = chr3 & 63;

                if (isNaN(chr2)) {
                    enc3 = enc4 = 64;
                } else if (isNaN(chr3)) {
                    enc4 = 64;
                }

                output = output +
                    keyStr.charAt(enc1) +
                    keyStr.charAt(enc2) +
                    keyStr.charAt(enc3) +
                    keyStr.charAt(enc4);
                chr1 = chr2 = chr3 = '';
                enc1 = enc2 = enc3 = enc4 = '';
            }

            return output;
        };

        this.decode = function (input) {
            var output = '',
                chr1, chr2, chr3 = '',
                enc1, enc2, enc3, enc4 = '',
                i = 0;

            // remove all characters that are not A-Z, a-z, 0-9, +, /, or =
            input = input.replace(/[^A-Za-z0-9\+\/\=]/g, '');

            while (i < input.length) {
                enc1 = keyStr.indexOf(input.charAt(i++));
                enc2 = keyStr.indexOf(input.charAt(i++));
                enc3 = keyStr.indexOf(input.charAt(i++));
                enc4 = keyStr.indexOf(input.charAt(i++));

                chr1 = (enc1 << 2) | (enc2 >> 4);
                chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
                chr3 = ((enc3 & 3) << 6) | enc4;

                output = output + String.fromCharCode(chr1);

                if (enc3 !== 64) {
                    output = output + String.fromCharCode(chr2);
                }
                if (enc4 !== 64) {
                    output = output + String.fromCharCode(chr3);
                }

                chr1 = chr2 = chr3 = '';
                enc1 = enc2 = enc3 = enc4 = '';
            }
        };
    })
    .factory('StorageService', ["$window", function ($window) {
        return {

            get: function (key) {
                return JSON.parse($window.localStorage.getItem(key));
            },

            save: function (key, data) {
                $window.localStorage.setItem(key, JSON.stringify(data));
            },

            remove: function (key) {
                $window.localStorage.removeItem(key);
            },

            clearAll : function () {
                $window.localStorage.clear();
            }
        };
    }]);


'use strict';

angular.module('reachoutApp')
    .filter('capitalize', function () {
        return function (input, scope) {
            if (input != null)
                input = input.toLowerCase();
            return input.substring(0, 1).toUpperCase() + input.substring(1);
        }
    });

'use strict';

angular.module('reachoutApp')
    .provider('AlertService', function () {
        this.toast = false;

        this.$get = ['$timeout', '$sce', '$translate', function($timeout, $sce,$translate) {

            var exports = {
                factory: factory,
                isToast: isToast,
                add: addAlert,
                closeAlert: closeAlert,
                closeAlertByIndex: closeAlertByIndex,
                clear: clear,
                get: get,
                success: success,
                error: error,
                info: info,
                warning : warning
            },
            
            toast = this.toast,
            alertId = 0, // unique id for each alert. Starts from 0.
            alerts = [],
            timeout = 5000; // default timeout

            function isToast() {
                return toast;
            }

            function clear() {
                alerts = [];
            }

            function get() {
                return alerts;
            }

            function success(msg, params, position) {
                return this.add({
                    type: "success",
                    msg: msg,
                    params: params,
                    timeout: timeout,
                    toast: toast,
                    position: position
                });
            }

            function error(msg, params, position) {
                return this.add({
                    type: "danger",
                    msg: msg,
                    params: params,
                    timeout: timeout,
                    toast: toast,
                    position: position
                });
            }

            function warning(msg, params, position) {
                return this.add({
                    type: "warning",
                    msg: msg,
                    params: params,
                    timeout: timeout,
                    toast: toast,
                    position: position
                });
            }

            function info(msg, params, position) {
                return this.add({
                    type: "info",
                    msg: msg,
                    params: params,
                    timeout: timeout,
                    toast: toast,
                    position: position
                });
            }

            function factory(alertOptions) {
                var alert = {
                    type: alertOptions.type,
                    msg: $sce.trustAsHtml(alertOptions.msg),
                    id: alertOptions.alertId,
                    timeout: alertOptions.timeout,
                    toast: alertOptions.toast,
                    position: alertOptions.position ? alertOptions.position : 'top right',
                    scoped: alertOptions.scoped,
                    close: function (alerts) {
                        return exports.closeAlert(this.id, alerts);
                    }
                }
                if(!alert.scoped) {
                    alerts.push(alert);
                }
                return alert;
            }

            function addAlert(alertOptions, extAlerts) {
                alertOptions.alertId = alertId++;
                alertOptions.msg = $translate.instant(alertOptions.msg, alertOptions.params);
                var that = this;
                var alert = this.factory(alertOptions);
                if (alertOptions.timeout && alertOptions.timeout > 0) {
                    $timeout(function () {
                        that.closeAlert(alertOptions.alertId, extAlerts);
                    }, alertOptions.timeout);
                }
                return alert;
            }

            function closeAlert(id, extAlerts) {
                var thisAlerts = extAlerts ? extAlerts : alerts;
                return this.closeAlertByIndex(thisAlerts.map(function(e) { return e.id; }).indexOf(id), thisAlerts);
            }

            function closeAlertByIndex(index, thisAlerts) {
                return thisAlerts.splice(index, 1);
            }

            return exports;
        }];

        this.showAsToast = function(isToast) {
            this.toast = isToast;
        };

    });

'use strict';

angular.module('reachoutApp')
    .directive('jhAlert', ["AlertService", function(AlertService) {
        return {
            restrict: 'E',
            template: '<div class="alerts" ng-cloak="">' +
                            '<div ng-repeat="alert in alerts" ng-class="[alert.position, {\'toast\': alert.toast}]">' +
                                '<uib-alert ng-cloak="" type="{{alert.type}}" close="alert.close()"><pre>{{ alert.msg }}</pre></uib-alert>' +
                            '</div>' +
                      '</div>',
            controller: ['$scope',
                function($scope) {
                    $scope.alerts = AlertService.get();
                    $scope.$on('$destroy', function () {
                        $scope.alerts = [];
                    });
                }
            ]
        }
    }])
    .directive('jhAlertError', ["AlertService", "$rootScope", "$translate", function(AlertService, $rootScope, $translate) {
        return {
            restrict: 'E',
            template: '<div class="alerts" ng-cloak="">' +
                            '<div ng-repeat="alert in alerts" ng-class="[alert.position, {\'toast\': alert.toast}]">' +
                                '<uib-alert ng-cloak="" type="{{alert.type}}" close="alert.close(alerts)"><pre>{{ alert.msg }}</pre></uib-alert>' +
                            '</div>' +
                      '</div>',
            controller: ['$scope',
                function($scope) {

                    $scope.alerts = [];

                    var cleanHttpErrorListener = $rootScope.$on('reachoutApp.httpError', function (event, httpResponse) {
                        var i;
                        event.stopPropagation();
                        switch (httpResponse.status) {
                            // connection refused, server not reachable
                            case 0:
                                addErrorAlert("Server not reachable",'error.server.not.reachable');
                                break;

                            case 400:
                                var errorHeader = httpResponse.headers('X-reachoutApp-error');
                                var entityKey = httpResponse.headers('X-reachoutApp-params');
                                if (errorHeader) {
                                    var entityName = $translate.instant('global.menu.entities.' + entityKey);
                                    addErrorAlert(errorHeader, errorHeader, {entityName: entityName});
                                } else if (httpResponse.data && httpResponse.data.fieldErrors) {
                                    for (i = 0; i < httpResponse.data.fieldErrors.length; i++) {
                                        var fieldError = httpResponse.data.fieldErrors[i];
                                        // convert 'something[14].other[4].id' to 'something[].other[].id' so translations can be written to it
                                        var convertedField = fieldError.field.replace(/\[\d*\]/g, "[]");
                                        var fieldName = $translate.instant('reachoutApp.' + fieldError.objectName + '.' + convertedField);
                                        addErrorAlert('Field ' + fieldName + ' cannot be empty', 'error.' + fieldError.message, {fieldName: fieldName});
                                    }
                                } else if (httpResponse.data && httpResponse.data.message) {
                                    addErrorAlert(httpResponse.data.message, httpResponse.data.message, httpResponse.data);
                                } else {
                                    addErrorAlert(httpResponse.data);
                                }
                                break;

                            default:
                                if (httpResponse.data && httpResponse.data.message) {
                                    addErrorAlert(httpResponse.data.message);
                                } else {
                                    addErrorAlert(JSON.stringify(httpResponse));
                                }
                        }
                    });

                    $scope.$on('$destroy', function () {
                        if(cleanHttpErrorListener !== undefined && cleanHttpErrorListener !== null){
                            cleanHttpErrorListener();
                            $scope.alerts = [];
                        }
                    });

                    var addErrorAlert = function (message, key, data) {
                        key = key && key != null ? key : message;
                        $scope.alerts.push(
                            AlertService.add(
                                {
                                    type: "danger",
                                    msg: key,
                                    params: data,
                                    timeout: 5000,
                                    toast: AlertService.isToast(),
                                    scoped: true
                                },
                                $scope.alerts
                            )
                        );
                    }
                }
            ]
        }
    }]);

'use strict';

angular.module('reachoutApp')
    .service('ParseLinks', function () {
        this.parse = function (header) {
            if (header.length == 0) {
                throw new Error("input must not be of zero length");
            }

            // Split parts by comma
            var parts = header.split(',');
            var links = {};
            // Parse each part into a named link
            angular.forEach(parts, function (p) {
                var section = p.split(';');
                if (section.length != 2) {
                    throw new Error("section could not be split on ';'");
                }
                var url = section[0].replace(/<(.*)>/, '$1').trim();
                var queryString = {};
                url.replace(
                    new RegExp("([^?=&]+)(=([^&]*))?", "g"),
                    function($0, $1, $2, $3) { queryString[$1] = $3; }
                );
                var page = queryString['page'];
                if( angular.isString(page) ) {
                    page = parseInt(page);
                }
                var name = section[1].replace(/rel="(.*)"/, '$1').trim();
                links[name] = page;
            });

            return links;
        }
    });

'use strict';

angular.module('reachoutApp')
    .service('DateUtils', ["$filter", function ($filter) {

    this.convertLocaleDateToServer = function(date) {
        if (date) {
            return $filter('date')(date, 'yyyy-MM-dd');
        } else {
            return null;
        }
    };

    this.convertLocaleDateFromServer = function(date) {
        if (date) {
            var dateString = date.split("-");
            return new Date(dateString[0], dateString[1] - 1, dateString[2]);
        }
        return null;
    };

    this.convertDateTimeFromServer = function(date) {
        if (date) {
            return new Date(date);
        } else {
            return null;
        }
    }

    // common date format for all date input fields
    this.dateformat = function() {
        return 'yyyy-MM-dd';
    }
}]);

'use strict';

angular.module('reachoutApp')
    .service('DataUtils', ["$filter", function ($filter) {
    this.byteSize = function (base64String) {
        if (!angular.isString(base64String)) {
            return '';
        }
        function endsWith(suffix, str) {
            return str.indexOf(suffix, str.length - suffix.length) !== -1;
        }
        function paddingSize(base64String) {
            if (endsWith('==', base64String)) {
                return 2;
            }
            if (endsWith('=', base64String)) {
                return 1;
            }
            return 0;
        }
        function size(base64String) {
            return base64String.length / 4 * 3 - paddingSize(base64String);
        }
        function formatAsBytes(size) {
            return size.toString().replace(/\B(?=(\d{3})+(?!\d))/g, " ") + " bytes";
        }

        return formatAsBytes(size(base64String));
    };
    this.abbreviate = function (text) {
        if (!angular.isString(text)) {
            return '';
        }
        if (text.length < 30) {
            return text;
        }
        return text ? (text.substring(0, 15) + '...' + text.slice(-10)) : '';
    };
}]);

'use strict';

angular.module('reachoutApp')
    .directive('jhSort', function () {
        return {
            restrict: 'A',
            scope: {
                predicate: '=jhSort',
                ascending: '=',
                callback: '&'
            },
            controller: ['$scope', function ($scope) {
                this.sort = function (field) {
                    if (field !== $scope.predicate) {
                        $scope.ascending = true;
                    } else {
                        $scope.ascending = !$scope.ascending;
                    }
                    $scope.predicate = field;
                    $scope.$apply();
                    $scope.callback();
                }
                this.applyClass = function (element) {
                    var allThIcons = element.parent().find('span.glyphicon'),
                    sortIcon = 'glyphicon-sort',
                    sortAsc = 'glyphicon-sort-by-attributes',
                    sortDesc = 'glyphicon-sort-by-attributes-alt',
                    remove = sortIcon + ' ' + sortDesc,
                    add = sortAsc,
                    thisIcon = element.find('span.glyphicon');
                    if (!$scope.ascending) {
                        remove = sortIcon + ' ' + sortAsc;
                        add = sortDesc;
                    }
                    allThIcons.removeClass(sortAsc + ' ' + sortDesc);
                    allThIcons.addClass(sortIcon);
                    thisIcon.removeClass(remove);
                    thisIcon.addClass(add);
                }
            }]
        }
    }).directive('jhSortBy', function () {
        return {
            restrict: 'A',
            scope: false,
            require: '^jhSort',
            link: function (scope, element, attrs, parentCtrl) {
                element.bind('click', function () {
                    parentCtrl.sort(attrs.jhSortBy);
                    parentCtrl.applyClass(element);
                });
            }
        };
    });

'use strict';

angular.module('reachoutApp')
    .config(["$stateProvider", function ($stateProvider) {
        $stateProvider
            .state('account', {
                abstract: true,
                parent: 'site'
            });
    }]);

'use strict';

angular.module('reachoutApp')
    .config(["$stateProvider", function ($stateProvider) {
        $stateProvider
            .state('activate', {
                parent: 'account',
                url: '/activate?key',
                data: {
                    authorities: [],
                    pageTitle: 'activate.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/account/activate/activate.html',
                        controller: 'ActivationController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('activate');
                        return $translate.refresh();
                    }]
                }
            });
    }]);

'use strict';

angular.module('reachoutApp')
    .controller('ActivationController', ["$scope", "$stateParams", "Auth", function ($scope, $stateParams, Auth) {
        Auth.activateAccount({key: $stateParams.key}).then(function () {
            $scope.error = null;
            $scope.success = 'OK';
        }).catch(function () {
            $scope.success = null;
            $scope.error = 'ERROR';
        });
    }]);


'use strict';

angular.module('reachoutApp')
    .config(["$stateProvider", function ($stateProvider) {
        $stateProvider
            .state('login', {
                parent: 'account',
                url: '/login',
                data: {
                    authorities: [], 
                    pageTitle: 'login.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/account/login/login.html',
                        controller: 'LoginController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('login');
                        return $translate.refresh();
                    }]
                }
            });
    }]);

'use strict';

angular.module('reachoutApp')
    .controller('LoginController', ["$rootScope", "$scope", "$state", "$timeout", "Auth", function ($rootScope, $scope, $state, $timeout, Auth) {
        $scope.user = {};
        $scope.errors = {};

        $scope.rememberMe = true;
        $timeout(function (){angular.element('[ng-model="username"]').focus();});
        $scope.login = function (event) {
            event.preventDefault();
            Auth.login({
                username: $scope.username,
                password: $scope.password,
                rememberMe: $scope.rememberMe
            }).then(function () {
                $scope.authenticationError = false;
                if ($rootScope.previousStateName === 'register') {
                    $state.go('home');
                } else {
                    $rootScope.back();
                }
            }).catch(function () {
                $scope.authenticationError = true;
            });
        };
    }]);

'use strict';

angular.module('reachoutApp')
    .config(["$stateProvider", function ($stateProvider) {
        $stateProvider
            .state('password', {
                parent: 'account',
                url: '/password',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'global.menu.account.password'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/account/password/password.html',
                        controller: 'PasswordController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('password');
                        return $translate.refresh();
                    }]
                }
            });
    }]);

'use strict';

angular.module('reachoutApp')
    .controller('PasswordController', ["$scope", "Auth", "Principal", function ($scope, Auth, Principal) {
        Principal.identity().then(function(account) {
            $scope.account = account;
        });

        $scope.success = null;
        $scope.error = null;
        $scope.doNotMatch = null;
        $scope.changePassword = function () {
            if ($scope.password !== $scope.confirmPassword) {
                $scope.error = null;
                $scope.success = null;
                $scope.doNotMatch = 'ERROR';
            } else {
                $scope.doNotMatch = null;
                Auth.changePassword($scope.password).then(function () {
                    $scope.error = null;
                    $scope.success = 'OK';
                }).catch(function () {
                    $scope.success = null;
                    $scope.error = 'ERROR';
                });
            }
        };
    }]);

/* globals $ */
'use strict';

angular.module('reachoutApp')
    .directive('passwordStrengthBar', function () {
        return {
            replace: true,
            restrict: 'E',
            template: '<div id="strength">' +
                '<small translate="global.messages.validate.newpassword.strength">Password strength:</small>' +
                '<ul id="strengthBar">' +
                '<li class="point"></li><li class="point"></li><li class="point"></li><li class="point"></li><li class="point"></li>' +
                '</ul>' +
                '</div>',
            link: function (scope, iElement, attr) {
                var strength = {
                    colors: ['#F00', '#F90', '#FF0', '#9F0', '#0F0'],
                    mesureStrength: function (p) {

                        var _force = 0;
                        var _regex = /[$-/:-?{-~!"^_`\[\]]/g; // "

                        var _lowerLetters = /[a-z]+/.test(p);
                        var _upperLetters = /[A-Z]+/.test(p);
                        var _numbers = /[0-9]+/.test(p);
                        var _symbols = _regex.test(p);

                        var _flags = [_lowerLetters, _upperLetters, _numbers, _symbols];
                        var _passedMatches = $.grep(_flags, function (el) {
                            return el === true;
                        }).length;

                        _force += 2 * p.length + ((p.length >= 10) ? 1 : 0);
                        _force += _passedMatches * 10;

                        // penality (short password)
                        _force = (p.length <= 6) ? Math.min(_force, 10) : _force;

                        // penality (poor variety of characters)
                        _force = (_passedMatches === 1) ? Math.min(_force, 10) : _force;
                        _force = (_passedMatches === 2) ? Math.min(_force, 20) : _force;
                        _force = (_passedMatches === 3) ? Math.min(_force, 40) : _force;

                        return _force;

                    },
                    getColor: function (s) {

                        var idx = 0;
                        if (s <= 10) {
                            idx = 0;
                        }
                        else if (s <= 20) {
                            idx = 1;
                        }
                        else if (s <= 30) {
                            idx = 2;
                        }
                        else if (s <= 40) {
                            idx = 3;
                        }
                        else {
                            idx = 4;
                        }

                        return { idx: idx + 1, col: this.colors[idx] };
                    }
                };
                scope.$watch(attr.passwordToCheck, function (password) {
                    if (password) {
                        var c = strength.getColor(strength.mesureStrength(password));
                        iElement.removeClass('ng-hide');
                        iElement.find('ul').children('li')
                            .css({ 'background-color': '#DDD' })
                            .slice(0, c.idx)
                            .css({ 'background-color': c.col });
                    }
                });
            }
        };
    });

'use strict';

angular.module('reachoutApp')
    .config(["$stateProvider", function ($stateProvider) {
        $stateProvider
            .state('register', {
                parent: 'account',
                url: '/register',
                data: {
                    authorities: [],
                    pageTitle: 'register.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/account/register/register.html',
                        controller: 'RegisterController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('register');
                        return $translate.refresh();
                    }]
                }
            });
    }]);

'use strict';

angular.module('reachoutApp')
    .controller('RegisterController', ["$scope", "$translate", "$timeout", "Auth", function ($scope, $translate, $timeout, Auth) {
        $scope.success = null;
        $scope.error = null;
        $scope.doNotMatch = null;
        $scope.errorUserExists = null;
        $scope.registerAccount = {};
        $timeout(function (){angular.element('[ng-model="registerAccount.login"]').focus();});

        $scope.register = function () {
            if ($scope.registerAccount.password !== $scope.confirmPassword) {
                $scope.doNotMatch = 'ERROR';
            } else {
                $scope.registerAccount.langKey = $translate.use();
                $scope.doNotMatch = null;
                $scope.error = null;
                $scope.errorUserExists = null;
                $scope.errorEmailExists = null;

                Auth.createAccount($scope.registerAccount).then(function () {
                    $scope.success = 'OK';
                }).catch(function (response) {
                    $scope.success = null;
                    if (response.status === 400 && response.data === 'login already in use') {
                        $scope.errorUserExists = 'ERROR';
                    } else if (response.status === 400 && response.data === 'e-mail address already in use') {
                        $scope.errorEmailExists = 'ERROR';
                    } else {
                        $scope.error = 'ERROR';
                    }
                });
            }
        };
    }]);

'use strict';

angular.module('reachoutApp')
    .config(["$stateProvider", function ($stateProvider) {
        $stateProvider
            .state('settings', {
                parent: 'account',
                url: '/settings',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'global.menu.account.settings'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/account/settings/settings.html',
                        controller: 'SettingsController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('settings');
                        return $translate.refresh();
                    }]
                }
            });
    }]);

'use strict';

angular.module('reachoutApp')
    .controller('SettingsController', ["$scope", "Principal", "Auth", "Language", "$translate", function ($scope, Principal, Auth, Language, $translate) {
        $scope.success = null;
        $scope.error = null;
        Principal.identity().then(function(account) {
            $scope.settingsAccount = copyAccount(account);
        });

        $scope.save = function () {
            Auth.updateAccount($scope.settingsAccount).then(function() {
                $scope.error = null;
                $scope.success = 'OK';
                Principal.identity(true).then(function(account) {
                    $scope.settingsAccount = copyAccount(account);
                });
                Language.getCurrent().then(function(current) {
                    if ($scope.settingsAccount.langKey !== current) {
                        $translate.use($scope.settingsAccount.langKey);
                    }
                });
            }).catch(function() {
                $scope.success = null;
                $scope.error = 'ERROR';
            });
        };

        /**
         * Store the "settings account" in a separate variable, and not in the shared "account" variable.
         */
        var copyAccount = function (account) {
            return {
                activated: account.activated,
                email: account.email,
                firstName: account.firstName,
                langKey: account.langKey,
                lastName: account.lastName,
                login: account.login
            }
        }
    }]);

'use strict';

angular.module('reachoutApp')
    .controller('ResetFinishController', ["$scope", "$stateParams", "$timeout", "Auth", function ($scope, $stateParams, $timeout, Auth) {

        $scope.keyMissing = $stateParams.key === undefined;
        $scope.doNotMatch = null;

        $scope.resetAccount = {};
        $timeout(function (){angular.element('[ng-model="resetAccount.password"]').focus();});

        $scope.finishReset = function() {
            if ($scope.resetAccount.password !== $scope.confirmPassword) {
                $scope.doNotMatch = 'ERROR';
            } else {
                Auth.resetPasswordFinish({key: $stateParams.key, newPassword: $scope.resetAccount.password}).then(function () {
                    $scope.success = 'OK';
                }).catch(function (response) {
                    $scope.success = null;
                    $scope.error = 'ERROR';

                });
            }

        };
    }]);

'use strict';

angular.module('reachoutApp')
    .config(["$stateProvider", function ($stateProvider) {
        $stateProvider
            .state('finishReset', {
                parent: 'account',
                url: '/reset/finish?key',
                data: {
                    authorities: []
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/account/reset/finish/reset.finish.html',
                        controller: 'ResetFinishController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('reset');
                        return $translate.refresh();
                    }]
                }
            });
    }]);

'use strict';

angular.module('reachoutApp')
    .controller('RequestResetController', ["$rootScope", "$scope", "$state", "$timeout", "Auth", function ($rootScope, $scope, $state, $timeout, Auth) {

        $scope.success = null;
        $scope.error = null;
        $scope.errorEmailNotExists = null;
        $scope.resetAccount = {};
        $timeout(function (){angular.element('[ng-model="resetAccount.email"]').focus();});

        $scope.requestReset = function () {

            $scope.error = null;
            $scope.errorEmailNotExists = null;

            Auth.resetPasswordInit($scope.resetAccount.email).then(function () {
                $scope.success = 'OK';
            }).catch(function (response) {
                $scope.success = null;
                if (response.status === 400 && response.data === 'e-mail address not registered') {
                    $scope.errorEmailNotExists = 'ERROR';
                } else {
                    $scope.error = 'ERROR';
                }
            });
        }

    }]);

'use strict';

angular.module('reachoutApp')
    .config(["$stateProvider", function ($stateProvider) {
        $stateProvider
            .state('requestReset', {
                parent: 'account',
                url: '/reset/request',
                data: {
                    authorities: []
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/account/reset/request/reset.request.html',
                        controller: 'RequestResetController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('reset');
                        return $translate.refresh();
                    }]
                }
            });
    }]);

'use strict';

angular.module('reachoutApp')
    .config(["$stateProvider", function ($stateProvider) {
        $stateProvider
            .state('admin', {
                abstract: true,
                parent: 'site'
            });
    }]);

'use strict';

angular.module('reachoutApp')
    .config(["$stateProvider", function ($stateProvider) {
        $stateProvider
            .state('audits', {
                parent: 'admin',
                url: '/audits',
                data: {
                    authorities: ['ROLE_ADMIN'],
                    pageTitle: 'audits.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/admin/audits/audits.html',
                        controller: 'AuditsController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('audits');
                        return $translate.refresh();
                    }]
                }
            });
    }]);

'use strict';

angular.module('reachoutApp')
    .controller('AuditsController', ["$scope", "$filter", "AuditsService", function ($scope, $filter, AuditsService) {
        $scope.onChangeDate = function () {
            var dateFormat = 'yyyy-MM-dd';
            var fromDate = $filter('date')($scope.fromDate, dateFormat);
            var toDate = $filter('date')($scope.toDate, dateFormat);

            AuditsService.findByDates(fromDate, toDate).then(function (data) {
                $scope.audits = data;
            });
        };

        // Date picker configuration
        $scope.today = function () {
            // Today + 1 day - needed if the current day must be included
            var today = new Date();
            $scope.toDate = new Date(today.getFullYear(), today.getMonth(), today.getDate() + 1);
        };

        $scope.previousMonth = function () {
            var fromDate = new Date();
            if (fromDate.getMonth() === 0) {
                fromDate = new Date(fromDate.getFullYear() - 1, 0, fromDate.getDate());
            } else {
                fromDate = new Date(fromDate.getFullYear(), fromDate.getMonth() - 1, fromDate.getDate());
            }

            $scope.fromDate = fromDate;
        };

        $scope.today();
        $scope.previousMonth();
        $scope.onChangeDate();
    }]);

'use strict';

angular.module('reachoutApp')
    .config(["$stateProvider", function ($stateProvider) {
        $stateProvider
            .state('configuration', {
                parent: 'admin',
                url: '/configuration',
                data: {
                    authorities: ['ROLE_ADMIN'],
                    pageTitle: 'configuration.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/admin/configuration/configuration.html',
                        controller: 'ConfigurationController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('configuration');
                        return $translate.refresh();
                    }]
                }
            });
    }]);

'use strict';

angular.module('reachoutApp')
    .controller('ConfigurationController', ["$scope", "ConfigurationService", function ($scope, ConfigurationService) {
        ConfigurationService.get().then(function(configuration) {
            $scope.configuration = configuration;
        });
    }]);

'use strict';

angular.module('reachoutApp')
    .config(["$stateProvider", function ($stateProvider) {
        $stateProvider
            .state('docs', {
                parent: 'admin',
                url: '/docs',
                data: {
                    authorities: ['ROLE_ADMIN'],
                    pageTitle: 'global.menu.admin.apidocs'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/admin/docs/docs.html'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', function ($translate) {
                        return $translate.refresh();
                    }]
                }
            });
    }]);

'use strict';

angular.module('reachoutApp')
    .config(["$stateProvider", function ($stateProvider) {
        $stateProvider
            .state('health', {
                parent: 'admin',
                url: '/health',
                data: {
                    authorities: ['ROLE_ADMIN'],
                    pageTitle: 'health.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/admin/health/health.html',
                        controller: 'HealthController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('health');
                        return $translate.refresh();
                    }]
                }
            });
    }]);

'use strict';

angular.module('reachoutApp')
    .controller('HealthController', ["$scope", "MonitoringService", "$uibModal", function ($scope, MonitoringService, $uibModal) {
        $scope.updatingHealth = true;
        $scope.separator = '.';

        $scope.refresh = function () {
            $scope.updatingHealth = true;
            MonitoringService.checkHealth().then(function (response) {
                $scope.healthData = $scope.transformHealthData(response);
                $scope.updatingHealth = false;
            }, function (response) {
                $scope.healthData =  $scope.transformHealthData(response.data);
                $scope.updatingHealth = false;
            });
        };

        $scope.refresh();

        $scope.getLabelClass = function (statusState) {
            if (statusState === 'UP') {
                return 'label-success';
            } else {
                return 'label-danger';
            }
        };

        $scope.transformHealthData = function (data) {
            var response = [];
            $scope.flattenHealthData(response, null, data);
            return response;
        };

        $scope.flattenHealthData = function (result, path, data) {
            angular.forEach(data, function (value, key) {
                if ($scope.isHealthObject(value)) {
                    if ($scope.hasSubSystem(value)) {
                        $scope.addHealthObject(result, false, value, $scope.getModuleName(path, key));
                        $scope.flattenHealthData(result, $scope.getModuleName(path, key), value);
                    } else {
                        $scope.addHealthObject(result, true, value, $scope.getModuleName(path, key));
                    }
                }
            });
            return result;
        };

        $scope.getModuleName = function (path, name) {
            var result;
            if (path && name) {
                result = path + $scope.separator + name;
            }  else if (path) {
                result = path;
            } else if (name) {
                result = name;
            } else {
                result = '';
            }
            return result;
        };


        $scope.showHealth = function(health) {
            var modalInstance = $uibModal.open({
                templateUrl: 'scripts/app/admin/health/health.modal.html',
                controller: 'HealthModalController',
                size: 'lg',
                resolve: {
                    currentHealth: function() {
                        return health;
                    },
                    baseName: function() {
                        return $scope.baseName;
                    },
                    subSystemName: function() {
                        return $scope.subSystemName;
                    }

                }
            });
        };

        $scope.addHealthObject = function (result, isLeaf, healthObject, name) {

            var healthData = {
                'name': name
            };
            var details = {};
            var hasDetails = false;

            angular.forEach(healthObject, function (value, key) {
                if (key === 'status' || key === 'error') {
                    healthData[key] = value;
                } else {
                    if (!$scope.isHealthObject(value)) {
                        details[key] = value;
                        hasDetails = true;
                    }
                }
            });

            // Add the of the details
            if (hasDetails) {
                angular.extend(healthData, { 'details': details});
            }

            // Only add nodes if they provide additional information
            if (isLeaf || hasDetails || healthData.error) {
                result.push(healthData);
            }
            return healthData;
        };

        $scope.hasSubSystem = function (healthObject) {
            var result = false;
            angular.forEach(healthObject, function (value) {
                if (value && value.status) {
                    result = true;
                }
            });
            return result;
        };

        $scope.isHealthObject = function (healthObject) {
            var result = false;
            angular.forEach(healthObject, function (value, key) {
                if (key === 'status') {
                    result = true;
                }
            });
            return result;
        };

        $scope.baseName = function (name) {
            if (name) {
              var split = name.split('.');
              return split[0];
            }
        };

        $scope.subSystemName = function (name) {
            if (name) {
              var split = name.split('.');
              split.splice(0, 1);
              var remainder = split.join('.');
              return remainder ? ' - ' + remainder : '';
            }
        };
    }]);

'use strict';

angular.module('reachoutApp')
    .controller('HealthModalController', ["$scope", "$uibModalInstance", "currentHealth", "baseName", "subSystemName", function($scope, $uibModalInstance, currentHealth, baseName, subSystemName) {

        $scope.currentHealth = currentHealth;
        $scope.baseName = baseName, $scope.subSystemName = subSystemName;

        $scope.cancel = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }]);

'use strict';

angular.module('reachoutApp')
    .config(["$stateProvider", function ($stateProvider) {
        $stateProvider
            .state('logs', {
                parent: 'admin',
                url: '/logs',
                data: {
                    authorities: ['ROLE_ADMIN'],
                    pageTitle: 'logs.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/admin/logs/logs.html',
                        controller: 'LogsController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('logs');
                        return $translate.refresh();
                    }]
                }
            });
    }]);

'use strict';

angular.module('reachoutApp')
    .controller('LogsController', ["$scope", "LogsService", function ($scope, LogsService) {
        $scope.loggers = LogsService.findAll();

        $scope.changeLevel = function (name, level) {
            LogsService.changeLevel({name: name, level: level}, function () {
                $scope.loggers = LogsService.findAll();
            });
        };
    }]);

'use strict';

angular.module('reachoutApp')
    .config(["$stateProvider", function ($stateProvider) {
        $stateProvider
            .state('metrics', {
                parent: 'admin',
                url: '/metrics',
                data: {
                    authorities: ['ROLE_ADMIN'],
                    pageTitle: 'metrics.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/admin/metrics/metrics.html',
                        controller: 'MetricsController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('metrics');
                        return $translate.refresh();
                    }]
                }
            });
    }]);

'use strict';

angular.module('reachoutApp')
    .controller('MetricsController', ["$scope", "MonitoringService", "$uibModal", function ($scope, MonitoringService, $uibModal) {
        $scope.metrics = {};
        $scope.updatingMetrics = true;

        $scope.refresh = function () {
            $scope.updatingMetrics = true;
            MonitoringService.getMetrics().then(function (promise) {
                $scope.metrics = promise;
                $scope.updatingMetrics = false;
            }, function (promise) {
                $scope.metrics = promise.data;
                $scope.updatingMetrics = false;
            });
        };

        $scope.$watch('metrics', function (newValue) {
            $scope.servicesStats = {};
            $scope.cachesStats = {};
            angular.forEach(newValue.timers, function (value, key) {
                if (key.indexOf('web.rest') !== -1 || key.indexOf('service') !== -1) {
                    $scope.servicesStats[key] = value;
                }
                if (key.indexOf('net.sf.ehcache.Cache') !== -1) {
                    // remove gets or puts
                    var index = key.lastIndexOf('.');
                    var newKey = key.substr(0, index);

                    // Keep the name of the domain
                    index = newKey.lastIndexOf('.');
                    $scope.cachesStats[newKey] = {
                        'name': newKey.substr(index + 1),
                        'value': value
                    };
                }
            });
        });

        $scope.refresh();

        $scope.refreshThreadDumpData = function() {
            MonitoringService.threadDump().then(function(data) {

                var modalInstance = $uibModal.open({
                    templateUrl: 'scripts/app/admin/metrics/metrics.modal.html',
                    controller: 'MetricsModalController',
                    size: 'lg',
                    resolve: {
                        threadDump: function() {
                            return data.content;
                        }

                    }
                });
            });
        };
    }]);

'use strict';

angular.module('reachoutApp')
    .controller('MetricsModalController', ["$scope", "$uibModalInstance", "threadDump", function($scope, $uibModalInstance, threadDump) {

        $scope.threadDump = threadDump;
        $scope.threadDumpRunnable = 0;
        $scope.threadDumpWaiting = 0;
        $scope.threadDumpTimedWaiting = 0;
        $scope.threadDumpBlocked = 0;

        angular.forEach(threadDump, function(value) {
            if (value.threadState === 'RUNNABLE') {
                $scope.threadDumpRunnable += 1;
            } else if (value.threadState === 'WAITING') {
                $scope.threadDumpWaiting += 1;
            } else if (value.threadState === 'TIMED_WAITING') {
                $scope.threadDumpTimedWaiting += 1;
            } else if (value.threadState === 'BLOCKED') {
                $scope.threadDumpBlocked += 1;
            }
        });

        $scope.threadDumpAll = $scope.threadDumpRunnable + $scope.threadDumpWaiting +
            $scope.threadDumpTimedWaiting + $scope.threadDumpBlocked;

        $scope.cancel = function() {
            $uibModalInstance.dismiss('cancel');
        };

        $scope.getLabelClass = function (threadState) {
            if (threadState === 'RUNNABLE') {
                return 'label-success';
            } else if (threadState === 'WAITING') {
                return 'label-info';
            } else if (threadState === 'TIMED_WAITING') {
                return 'label-warning';
            } else if (threadState === 'BLOCKED') {
                return 'label-danger';
            }
        };
    }]);

'use strict';

angular.module('reachoutApp')
    .controller('UserManagementDetailController', ["$scope", "$stateParams", "User", function ($scope, $stateParams, User) {
        $scope.user = {};
        $scope.load = function (login) {
            User.get({login: login}, function(result) {
                $scope.user = result;
            });
        };
        $scope.load($stateParams.login);
    }]);

'use strict';

angular.module('reachoutApp').controller('UserManagementDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'User', 'Language',
        function($scope, $stateParams, $uibModalInstance, entity, User, Language) {

        $scope.user = entity;
        $scope.authorities = ["ROLE_USER", "ROLE_ADMIN","ROLE_MARKETING", "ROLE_PROVIDER"];
        Language.getAll().then(function (languages) {
            $scope.languages = languages;
        });
        var onSaveSuccess = function (result) {
            $scope.isSaving = false;
            $uibModalInstance.close(result);
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.user.id != null) {
                User.update($scope.user, onSaveSuccess, onSaveError);
            } else {
                User.save($scope.user, onSaveSuccess, onSaveError);
            }
        };

        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
}]);

'use strict';

angular.module('reachoutApp')
	.controller('user-managementDeleteController', ["$scope", "$uibModalInstance", "entity", "User", function($scope, $uibModalInstance, entity, User) {

        $scope.user = entity;
        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.confirmDelete = function (login) {
            User.delete({login: login},
                function () {
                    $uibModalInstance.close(true);
                });
        };

    }]);

'use strict';

angular.module('reachoutApp')
    .controller('UserManagementController', ["$scope", "User", "ParseLinks", "Language", function ($scope, User, ParseLinks, Language) {
        $scope.users = [];
        $scope.authorities = ["ROLE_USER", "ROLE_ADMIN","ROLE_MARKETING", "ROLE_PROVIDER"];
        Language.getAll().then(function (languages) {
            $scope.languages = languages;
        });

        $scope.page = 1;
        $scope.loadAll = function () {
            User.query({page: $scope.page - 1, size: 20}, function (result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.totalItems = headers('X-Total-Count');
                $scope.users = result;
            });
        };

        $scope.loadPage = function (page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.setActive = function (user, isActivated) {
            user.activated = isActivated;
            User.update(user, function () {
                $scope.loadAll();
                $scope.clear();
            });
        };

        $scope.clear = function () {
            $scope.user = {
                id: null, login: null, firstName: null, lastName: null, email: null,
                activated: null, langKey: null, createdBy: null, createdDate: null,
                lastModifiedBy: null, lastModifiedDate: null, resetDate: null,
                resetKey: null, authorities: null
            };
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    }]);

'use strict';

angular.module('reachoutApp')
    .config(["$stateProvider", function ($stateProvider) {
        $stateProvider
            .state('user-management', {
                parent: 'admin',
                url: '/user-management',
                data: {
                    authorities: ['ROLE_ADMIN'],
                    pageTitle: 'user-management.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/admin/user-management/user-management.html',
                        controller: 'UserManagementController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('user.management');
                        return $translate.refresh();
                    }]
                }
            })
            .state('user-management-detail', {
                parent: 'admin',
                url: '/user/:login',
                data: {
                    authorities: ['ROLE_ADMIN'],
                    pageTitle: 'user-management.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/admin/user-management/user-management-detail.html',
                        controller: 'UserManagementDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('user.management');
                        return $translate.refresh();
                    }]
                }
            })
            .state('user-management.new', {
                parent: 'user-management',
                url: '/new',
                data: {
                    authorities: ['ROLE_ADMIN'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/admin/user-management/user-management-dialog.html',
                        controller: 'UserManagementDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    id: null, login: null, firstName: null, lastName: null, email: null,
                                    activated: true, langKey: null, createdBy: null, createdDate: null,
                                    lastModifiedBy: null, lastModifiedDate: null, resetDate: null,
                                    resetKey: null, authorities: null
                                };
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('user-management', null, { reload: true });
                    }, function() {
                        $state.go('user-management');
                    })
                }]
            })
            .state('user-management.edit', {
                parent: 'user-management',
                url: '/{login}/edit',
                data: {
                    authorities: ['ROLE_ADMIN'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/admin/user-management/user-management-dialog.html',
                        controller: 'UserManagementDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['User', function(User) {
                                return User.get({login : $stateParams.login});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('user-management', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            })
            .state('user-management.delete', {
                parent: 'user-management',
                url: '/{login}/delete',
                data: {
                    authorities: ['ROLE_ADMIN'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/admin/user-management/user-management-delete-dialog.html',
                        controller: 'user-managementDeleteController',
                        size: 'md',
                        resolve: {
                            entity: ['User', function(User) {
                                return User.get({login : $stateParams.login});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('user-management', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    }]);

'use strict';

angular.module('reachoutApp')
    .config(["$stateProvider", function ($stateProvider) {
        $stateProvider
            .state('entity', {
                abstract: true,
                parent: 'site'
            });
    }]);

'use strict';

angular.module('reachoutApp')
    .config(["$stateProvider", function ($stateProvider) {
        $stateProvider
            .state('error', {
                parent: 'site',
                url: '/error',
                data: {
                    authorities: [],
                    pageTitle: 'error.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/error/error.html'
                    }
                },
                resolve: {
                    mainTranslatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate,$translatePartialLoader) {
                        $translatePartialLoader.addPart('error');
                        return $translate.refresh();
                    }]
                }
            })
            .state('accessdenied', {
                parent: 'site',
                url: '/accessdenied',
                data: {
                    authorities: []
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/error/accessdenied.html'
                    }
                },
                resolve: {
                    mainTranslatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate,$translatePartialLoader) {
                        $translatePartialLoader.addPart('error');
                        return $translate.refresh();
                    }]
                }
            });
    }]);

'use strict';

angular.module('reachoutApp')
    .config(["$stateProvider", function ($stateProvider) {
        $stateProvider
            .state('home', {
                parent: 'site',
                url: '/',
                data: {
                    authorities: []
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/main/main.html',
                        controller: 'MainController'
                    }
                },
                resolve: {
                    mainTranslatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate,$translatePartialLoader) {
                        $translatePartialLoader.addPart('main');
                        return $translate.refresh();
                    }]
                }
            });
    }]);

'use strict';

angular.module('reachoutApp')
    .controller('MainController', ["$scope", "Principal", function ($scope, Principal) {
        Principal.identity().then(function(account) {
            $scope.account = account;
            $scope.isAuthenticated = Principal.isAuthenticated;
        });
    }]);

MessageFormat.locale.en=function(n){return n===1?"one":"other"}

MessageFormat.locale.fr=function(n){return n===0||n==1?"one":"other"}

'use strict';

angular.module('reachoutApp')
    .factory('Language', ["$q", "$http", "$translate", "LANGUAGES", function ($q, $http, $translate, LANGUAGES) {
        return {
            getCurrent: function () {
                var deferred = $q.defer();
                var language = $translate.storage().get('NG_TRANSLATE_LANG_KEY');

                if (angular.isUndefined(language)) {
                    language = 'en';
                }

                deferred.resolve(language);
                return deferred.promise;
            },
            getAll: function () {
                var deferred = $q.defer();
                deferred.resolve(LANGUAGES);
                return deferred.promise;
            }
        };
    }])

/*
 Languages codes are ISO_639-1 codes, see http://en.wikipedia.org/wiki/List_of_ISO_639-1_codes
 They are written in English to avoid character encoding issues (not a perfect solution)
 */
    .constant('LANGUAGES', [
        'en', 'fr'
        // jhipster-needle-006 - JHipster will add new languages here
    ]
);

'use strict';

angular.module('reachoutApp')
    .controller('LanguageController', ["$scope", "$translate", "Language", "tmhDynamicLocale", function ($scope, $translate, Language, tmhDynamicLocale) {
        $scope.changeLanguage = function (languageKey) {
            $translate.use(languageKey);
            tmhDynamicLocale.set(languageKey);
        };

        Language.getAll().then(function (languages) {
            $scope.languages = languages;
        });
    }])
    .filter('findLanguageFromKey', function () {
        return function (lang) {
            return {
                "ca": "Català",
                "da": "Dansk",
                "de": "Deutsch",
                "en": "English",
                "es": "Español",
                "fr": "Français",
                "gl": "Galego",
                "hu": "Magyar",
                "it": "Italiano",
                "ja": "日本語",
                "ko": "한국어",
                "nl": "Nederlands",
                "pl": "Polski",
                "pt-br": "Português (Brasil)",
                "pt-pt": "Português",
                "ro": "Română",
                "ru": "Русский",
                "sv": "Svenska",
                "ta": "தமிழ்",
                "tr": "Türkçe",
                "zh-cn": "中文（简体）",
                "zh-tw": "繁體中文"
            }[lang];
        }
    });

'use strict';

angular.module('reachoutApp')
    .factory('AuthServerProvider', ["$http", "localStorageService", "Base64", function loginService($http, localStorageService, Base64) {
        return {
            login: function(credentials) {
                var data = "username=" +  encodeURIComponent(credentials.username) + "&password="
                    + encodeURIComponent(credentials.password);
                return $http.post('api/authenticate', data, {
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded",
                        "Accept": "application/json"
                    }
                }).success(function (response) {
                    localStorageService.set('token', response);
                    return response;
                });
            },
            logout: function() {
                //Stateless API : No server logout
                localStorageService.clearAll();
            },
            getToken: function () {
                return localStorageService.get('token');
            },
            hasValidToken: function () {
                var token = this.getToken();
                return token && token.expires && token.expires > new Date().getTime();
            }
        };
    }]);

angular.module('reachoutApp')
    .config(["$stateProvider", function ($stateProvider) {
        $stateProvider
            .state('tracker', {
                parent: 'admin',
                url: '/tracker',
                data: {
                    authorities: ['ROLE_ADMIN'],
                    pageTitle: 'tracker.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/admin/tracker/tracker.html',
                        controller: 'TrackerController'
                    }
                },
                resolve: {
                    mainTranslatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('tracker');
                        return $translate.refresh();
                    }]
                },
                onEnter: ["Tracker", function(Tracker) {
                    Tracker.subscribe();
                }],
                onExit: ["Tracker", function(Tracker) {
                    Tracker.unsubscribe();
                }],
            });
    }]);

angular.module('reachoutApp')
    .controller('TrackerController', ["$scope", "$cookies", "$http", "Tracker", function ($scope, $cookies, $http, Tracker) {
        // This controller uses a Websocket connection to receive user activities in real-time.

        $scope.activities = [];
        Tracker.receive().then(null, null, function(activity) {
            showActivity(activity);
        });

        function showActivity(activity) {
            var existingActivity = false;
            for (var index = 0; index < $scope.activities.length; index++) {
                if($scope.activities[index].sessionId == activity.sessionId) {
                    existingActivity = true;
                    if (activity.page == 'logout') {
                        $scope.activities.splice(index, 1);
                    } else {
                        $scope.activities[index] = activity;
                    }
                }
            }
            if (!existingActivity && (activity.page != 'logout')) {
                $scope.activities.push(activity);
            }
        };
    }]);

'use strict';

angular.module('reachoutApp')
    .factory('Tracker', ["$rootScope", "$cookies", "$http", "$q", function ($rootScope, $cookies, $http, $q) {
        var stompClient = null;
        var subscriber = null;
        var listener = $q.defer();
        var connected = $q.defer();
        var alreadyConnectedOnce = false;
        function sendActivity() {
            if (stompClient != null && stompClient.connected) {
                stompClient
                    .send('/topic/activity',
                    {},
                    JSON.stringify({'page': $rootScope.toState.name}));
            }
        }
        return {
            connect: function () {
                //building absolute path so that websocket doesnt fail when deploying with a context path
                var loc = window.location;
                var url = '//' + loc.host + loc.pathname + 'websocket/tracker';
                var socket = new SockJS(url);
                stompClient = Stomp.over(socket);
                var headers = {};
                stompClient.connect(headers, function(frame) {
                    connected.resolve("success");
                    sendActivity();
                    if (!alreadyConnectedOnce) {
                        $rootScope.$on('$stateChangeStart', function (event) {
                            sendActivity();
                        });
                        alreadyConnectedOnce = true;
                    }
                });
            },
            subscribe: function() {
                connected.promise.then(function() {
                    subscriber = stompClient.subscribe("/topic/tracker", function(data) {
                        listener.notify(JSON.parse(data.body));
                    });
                }, null, null);
            },
            unsubscribe: function() {
                if (subscriber != null) {
                    subscriber.unsubscribe();
                }
            },
            receive: function() {
                return listener.promise;
            },
            sendActivity: function () {
                if (stompClient != null) {
                    sendActivity();
                }
            },
            disconnect: function() {
                if (stompClient != null) {
                    stompClient.disconnect();
                    stompClient = null;
                }
            }
        };
    }]);

'use strict';

angular.module('reachoutApp')
    .config(["$stateProvider", function ($stateProvider) {
        $stateProvider
            .state('notification', {
                parent: 'entity',
                url: '/notifications',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'reachoutApp.notification.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/notification/notifications.html',
                        controller: 'NotificationController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('notification');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('notification.detail', {
                parent: 'entity',
                url: '/notification/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'reachoutApp.notification.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/notification/notification-detail.html',
                        controller: 'NotificationDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('notification');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'Notification', function($stateParams, Notification) {
                        return Notification.get({id : $stateParams.id});
                    }]
                }
            })
            .state('notification.new', {
                parent: 'notification',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/notification/notification-dialog.html',
                        controller: 'NotificationDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    categery: null,
                                    title: null,
                                    description: null,
                                    validFrom: null,
                                    validTo: null,
                                    secureUrl: null,
                                    expieryDate: null,
                                    delivered: null,
                                    active: null,
                                    created: null,
                                    updated: null,
                                    offensive: null,
                                    publicId: null,
                                    url: null,
                                    consumerId: null,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('notification', null, { reload: true });
                    }, function() {
                        $state.go('notification');
                    })
                }]
            })
            .state('notification.edit', {
                parent: 'notification',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/notification/notification-dialog.html',
                        controller: 'NotificationDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Notification', function(Notification) {
                                return Notification.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('notification', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            })
            .state('notification.delete', {
                parent: 'notification',
                url: '/{id}/delete',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/notification/notification-delete-dialog.html',
                        controller: 'NotificationDeleteController',
                        size: 'md',
                        resolve: {
                            entity: ['Notification', function(Notification) {
                                return Notification.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('notification', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    }]);

'use strict';

	angular.module('reachoutApp')
    	.controller('NotificationController', ["$scope", "$http", "$state", "$filter", "Notification", function ($scope, $http, $state, $filter, Notification) {

        $scope.notifications = [];
        $scope.sortType = "title";
        $scope.sortReverse  = false;
        $scope.search = "";
        $scope.filterTerm= "";
        $scope.loadAll = function() {
            Notification.query(function(result) {
               $scope.notifications = result;
            });
        };
        $scope.loadAll();

        /*
         * Done by karthik for Filtering
         */

        $scope.approve = function(id,active){
        	var url = "api/notifications/approve"
        	active = !active
        	$scope.data = {
        			id: id,
        			active: active
        	};
        	 $http.post(url, $scope.data).success(function(data){
        		 $scope.notifications = data;
                 $scope.loadAll();
        	 });
        };
        
        $scope.refresh = function () {
            $scope.loadAll();
            $scope.clear();
        };
        
        $scope.selectedNotifications = [];
        $scope.categoryList = [{
            name: 'Food'
        }, {
            name: 'Travel'
        }, {
            name: 'Electronics'
        }];
        
        $scope.setselectedNotification = function () {
            var id = this.category.name;
            if (_.contains($scope.selectedNotifications, id)) {
                $scope.selectedNotifications = _.without($scope.selectedNotifications, id);
            } else {
                $scope.selectedNotifications.push(id);
            }
            return false;
        };

        $scope.isChecked = function (name) {
            if (_.contains($scope.selectedNotifications, name)) {
                return 'glyphicon glyphicon-ok pull-right';
            }
            return false;
        };

        $scope.checkAll = function () {
            $scope.selectedNotifications = _.pluck($scope.categoryList, 'name');
        };
    
        
        /* Done by karthik*/
        
        $scope.clear = function () {
            $scope.notification = {
                categery: null,
                title: null,
                description: null,
                validFrom: null,
                validTo: null,
                secureUrl: null,
                expieryDate: null,
                delivered: null,
                active: null,
                created: null,
                updated: null,
                offensive: null,
                publicId: null,
                url: null,
                consumerId: null,
                id: null
            };
        };
    }]);

	/*
	 * Done by Karthik 
	 */
angular.module('reachoutApp').filter('notificationFilter', [function () {
    return function (notifications, selectedNotifications) {
        if (!angular.isUndefined(notifications) && !angular.isUndefined(selectedNotifications) && selectedNotifications.length > 0) {
            var tempNotifications = [];
            angular.forEach(selectedNotifications, function (name) {
                angular.forEach(notifications, function (notification) {
                    if (angular.equals(notification.categery, name)) {
                    	tempNotifications.push(notification);
                    }
                });
            });
            return tempNotifications;
        } else {
            return notifications;
        }
    };
}]);
/*
 * Done by Karthik 
 */
'use strict';

angular.module('reachoutApp').controller('NotificationDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'Notification',
        function($scope, $stateParams, $uibModalInstance, entity, Notification) {

        $scope.notification = entity;
        $scope.load = function(id) {
            Notification.get({id : id}, function(result) {
                $scope.notification = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('reachoutApp:notificationUpdate', result);
            $uibModalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.notification.id != null) {
                Notification.update($scope.notification, onSaveSuccess, onSaveError);
            } else {
                Notification.save($scope.notification, onSaveSuccess, onSaveError);
            }
        };

        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.datePickerForValidFrom = {};

        $scope.datePickerForValidFrom.status = {
            opened: false
        };

        $scope.datePickerForValidFromOpen = function($event) {
            $scope.datePickerForValidFrom.status.opened = true;
        };
        $scope.datePickerForValidTo = {};

        $scope.datePickerForValidTo.status = {
            opened: false
        };

        $scope.datePickerForValidToOpen = function($event) {
            $scope.datePickerForValidTo.status.opened = true;
        };
        $scope.datePickerForExpieryDate = {};

        $scope.datePickerForExpieryDate.status = {
            opened: false
        };

        $scope.datePickerForExpieryDateOpen = function($event) {
            $scope.datePickerForExpieryDate.status.opened = true;
        };
        $scope.datePickerForCreated = {};

        $scope.datePickerForCreated.status = {
            opened: false
        };

        $scope.datePickerForCreatedOpen = function($event) {
            $scope.datePickerForCreated.status.opened = true;
        };
        $scope.datePickerForUpdated = {};

        $scope.datePickerForUpdated.status = {
            opened: false
        };

        $scope.datePickerForUpdatedOpen = function($event) {
            $scope.datePickerForUpdated.status.opened = true;
        };
}]);

'use strict';

angular.module('reachoutApp')
	.controller('NotificationDeleteController', ["$scope", "$uibModalInstance", "entity", "Notification", function($scope, $uibModalInstance, entity, Notification) {

        $scope.notification = entity;
        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.confirmDelete = function (id) {
            Notification.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };

    }]);

'use strict';

angular.module('reachoutApp')
    .controller('NotificationDetailController', ["$scope", "$rootScope", "$stateParams", "entity", "Notification", function ($scope, $rootScope, $stateParams, entity, Notification) {
        $scope.notification = entity;
        $scope.load = function (id) {
            Notification.get({id: id}, function(result) {
                $scope.notification = result;
            });
        };
        var unsubscribe = $rootScope.$on('reachoutApp:notificationUpdate', function(event, result) {
            $scope.notification = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }]);

'use strict';

angular.module('reachoutApp')
    .factory('Notification', ["$resource", "DateUtils", function ($resource, DateUtils) {
        return $resource('api/notifications/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.validFrom = DateUtils.convertDateTimeFromServer(data.validFrom);
                    data.validTo = DateUtils.convertDateTimeFromServer(data.validTo);
                    data.expieryDate = DateUtils.convertDateTimeFromServer(data.expieryDate);
                    data.created = DateUtils.convertDateTimeFromServer(data.created);
                    data.updated = DateUtils.convertDateTimeFromServer(data.updated);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }]);

'use strict';

angular.module('reachoutApp')
    .config(["$stateProvider", function ($stateProvider) {
        $stateProvider
            .state('cloudinary', {
                parent: 'entity',
                url: '/cloudinarys',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'reachoutApp.cloudinary.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/cloudinary/cloudinarys.html',
                        controller: 'CloudinaryController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('cloudinary');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('cloudinary.detail', {
                parent: 'entity',
                url: '/cloudinary/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'reachoutApp.cloudinary.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/cloudinary/cloudinary-detail.html',
                        controller: 'CloudinaryDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('cloudinary');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'Cloudinary', function($stateParams, Cloudinary) {
                        return Cloudinary.get({id : $stateParams.id});
                    }]
                }
            })
            .state('cloudinary.new', {
                parent: 'cloudinary',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/cloudinary/cloudinary-dialog.html',
                        controller: 'CloudinaryDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    consumerId: null,
                                    signature: null,
                                    format: null,
                                    resourceType: null,
                                    secureUrl: null,
                                    created: null,
                                    type: null,
                                    version: null,
                                    url: null,
                                    publicId: null,
                                    tags: null,
                                    orginalFileName: null,
                                    bytes: null,
                                    width: null,
                                    eTag: null,
                                    height: null,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('cloudinary', null, { reload: true });
                    }, function() {
                        $state.go('cloudinary');
                    })
                }]
            })
            .state('cloudinary.edit', {
                parent: 'cloudinary',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/cloudinary/cloudinary-dialog.html',
                        controller: 'CloudinaryDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Cloudinary', function(Cloudinary) {
                                return Cloudinary.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('cloudinary', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            })
            .state('cloudinary.delete', {
                parent: 'cloudinary',
                url: '/{id}/delete',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/cloudinary/cloudinary-delete-dialog.html',
                        controller: 'CloudinaryDeleteController',
                        size: 'md',
                        resolve: {
                            entity: ['Cloudinary', function(Cloudinary) {
                                return Cloudinary.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('cloudinary', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    }]);

'use strict';

angular.module('reachoutApp')
    .controller('CloudinaryController', ["$scope", "$state", "Cloudinary", function ($scope, $state, Cloudinary) {

        $scope.cloudinarys = [];
        $scope.loadAll = function() {
            Cloudinary.query(function(result) {
               $scope.cloudinarys = result;
            });
        };
        $scope.loadAll();


        $scope.refresh = function () {
            $scope.loadAll();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.cloudinary = {
                consumerId: null,
                signature: null,
                format: null,
                resourceType: null,
                secureUrl: null,
                created: null,
                type: null,
                version: null,
                url: null,
                publicId: null,
                tags: null,
                orginalFileName: null,
                bytes: null,
                width: null,
                eTag: null,
                height: null,
                id: null
            };
        };
    }]);

'use strict';

angular.module('reachoutApp').controller('CloudinaryDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'Cloudinary',
        function($scope, $stateParams, $uibModalInstance, entity, Cloudinary) {

        $scope.cloudinary = entity;
        $scope.load = function(id) {
            Cloudinary.get({id : id}, function(result) {
                $scope.cloudinary = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('reachoutApp:cloudinaryUpdate', result);
            $uibModalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.cloudinary.id != null) {
                Cloudinary.update($scope.cloudinary, onSaveSuccess, onSaveError);
            } else {
                Cloudinary.save($scope.cloudinary, onSaveSuccess, onSaveError);
            }
        };

        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.datePickerForCreated = {};

        $scope.datePickerForCreated.status = {
            opened: false
        };

        $scope.datePickerForCreatedOpen = function($event) {
            $scope.datePickerForCreated.status.opened = true;
        };
}]);

'use strict';

angular.module('reachoutApp')
	.controller('CloudinaryDeleteController', ["$scope", "$uibModalInstance", "entity", "Cloudinary", function($scope, $uibModalInstance, entity, Cloudinary) {

        $scope.cloudinary = entity;
        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.confirmDelete = function (id) {
            Cloudinary.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };

    }]);

'use strict';

angular.module('reachoutApp')
    .controller('CloudinaryDetailController', ["$scope", "$rootScope", "$stateParams", "entity", "Cloudinary", function ($scope, $rootScope, $stateParams, entity, Cloudinary) {
        $scope.cloudinary = entity;
        $scope.load = function (id) {
            Cloudinary.get({id: id}, function(result) {
                $scope.cloudinary = result;
            });
        };
        var unsubscribe = $rootScope.$on('reachoutApp:cloudinaryUpdate', function(event, result) {
            $scope.cloudinary = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }]);

'use strict';

angular.module('reachoutApp')
    .factory('Cloudinary', ["$resource", "DateUtils", function ($resource, DateUtils) {
        return $resource('api/cloudinarys/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.created = DateUtils.convertDateTimeFromServer(data.created);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }]);

'use strict';

angular.module('reachoutApp')
    .config(["$stateProvider", function ($stateProvider) {
        $stateProvider
            .state('consumerFavourite', {
                parent: 'entity',
                url: '/consumerFavourites',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'reachoutApp.consumerFavourite.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/consumerFavourite/consumerFavourites.html',
                        controller: 'ConsumerFavouriteController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('consumerFavourite');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('consumerFavourite.detail', {
                parent: 'entity',
                url: '/consumerFavourite/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'reachoutApp.consumerFavourite.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/consumerFavourite/consumerFavourite-detail.html',
                        controller: 'ConsumerFavouriteDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('consumerFavourite');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'ConsumerFavourite', function($stateParams, ConsumerFavourite) {
                        return ConsumerFavourite.get({id : $stateParams.id});
                    }]
                }
            })
            .state('consumerFavourite.new', {
                parent: 'consumerFavourite',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/consumerFavourite/consumerFavourite-dialog.html',
                        controller: 'ConsumerFavouriteDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    consumerId: null,
                                    providerId: null,
                                    created: null,
                                    updated: null,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('consumerFavourite', null, { reload: true });
                    }, function() {
                        $state.go('consumerFavourite');
                    })
                }]
            })
            .state('consumerFavourite.edit', {
                parent: 'consumerFavourite',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/consumerFavourite/consumerFavourite-dialog.html',
                        controller: 'ConsumerFavouriteDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['ConsumerFavourite', function(ConsumerFavourite) {
                                return ConsumerFavourite.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('consumerFavourite', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            })
            .state('consumerFavourite.delete', {
                parent: 'consumerFavourite',
                url: '/{id}/delete',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/consumerFavourite/consumerFavourite-delete-dialog.html',
                        controller: 'ConsumerFavouriteDeleteController',
                        size: 'md',
                        resolve: {
                            entity: ['ConsumerFavourite', function(ConsumerFavourite) {
                                return ConsumerFavourite.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('consumerFavourite', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    }]);

'use strict';

angular.module('reachoutApp')
    .controller('ConsumerFavouriteController', ["$scope", "$state", "ConsumerFavourite", function ($scope, $state, ConsumerFavourite) {

        $scope.consumerFavourites = [];
        $scope.loadAll = function() {
            ConsumerFavourite.query(function(result) {
               $scope.consumerFavourites = result;
            });
        };
        $scope.loadAll();


        $scope.refresh = function () {
            $scope.loadAll();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.consumerFavourite = {
                consumerId: null,
                providerId: null,
                created: null,
                updated: null,
                id: null
            };
        };
    }]);

'use strict';

angular.module('reachoutApp').controller('ConsumerFavouriteDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'ConsumerFavourite',
        function($scope, $stateParams, $uibModalInstance, entity, ConsumerFavourite) {

        $scope.consumerFavourite = entity;
        $scope.load = function(id) {
            ConsumerFavourite.get({id : id}, function(result) {
                $scope.consumerFavourite = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('reachoutApp:consumerFavouriteUpdate', result);
            $uibModalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.consumerFavourite.id != null) {
                ConsumerFavourite.update($scope.consumerFavourite, onSaveSuccess, onSaveError);
            } else {
                ConsumerFavourite.save($scope.consumerFavourite, onSaveSuccess, onSaveError);
            }
        };

        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.datePickerForCreated = {};

        $scope.datePickerForCreated.status = {
            opened: false
        };

        $scope.datePickerForCreatedOpen = function($event) {
            $scope.datePickerForCreated.status.opened = true;
        };
        $scope.datePickerForUpdated = {};

        $scope.datePickerForUpdated.status = {
            opened: false
        };

        $scope.datePickerForUpdatedOpen = function($event) {
            $scope.datePickerForUpdated.status.opened = true;
        };
}]);

'use strict';

angular.module('reachoutApp')
	.controller('ConsumerFavouriteDeleteController', ["$scope", "$uibModalInstance", "entity", "ConsumerFavourite", function($scope, $uibModalInstance, entity, ConsumerFavourite) {

        $scope.consumerFavourite = entity;
        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.confirmDelete = function (id) {
            ConsumerFavourite.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };

    }]);

'use strict';

angular.module('reachoutApp')
    .controller('ConsumerFavouriteDetailController', ["$scope", "$rootScope", "$stateParams", "entity", "ConsumerFavourite", function ($scope, $rootScope, $stateParams, entity, ConsumerFavourite) {
        $scope.consumerFavourite = entity;
        $scope.load = function (id) {
            ConsumerFavourite.get({id: id}, function(result) {
                $scope.consumerFavourite = result;
            });
        };
        var unsubscribe = $rootScope.$on('reachoutApp:consumerFavouriteUpdate', function(event, result) {
            $scope.consumerFavourite = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }]);

'use strict';

angular.module('reachoutApp')
    .factory('ConsumerFavourite', ["$resource", "DateUtils", function ($resource, DateUtils) {
        return $resource('api/consumerFavourites/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.created = DateUtils.convertDateTimeFromServer(data.created);
                    data.updated = DateUtils.convertDateTimeFromServer(data.updated);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }]);

'use strict';

angular.module('reachoutApp')
    .config(["$stateProvider", function ($stateProvider) {
        $stateProvider
            .state('consumerFeedback', {
                parent: 'entity',
                url: '/consumerFeedbacks',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'reachoutApp.consumerFeedback.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/consumerFeedback/consumerFeedbacks.html',
                        controller: 'ConsumerFeedbackController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('consumerFeedback');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('consumerFeedback.detail', {
                parent: 'entity',
                url: '/consumerFeedback/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'reachoutApp.consumerFeedback.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/consumerFeedback/consumerFeedback-detail.html',
                        controller: 'ConsumerFeedbackDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('consumerFeedback');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'ConsumerFeedback', function($stateParams, ConsumerFeedback) {
                        return ConsumerFeedback.get({id : $stateParams.id});
                    }]
                }
            })
            .state('consumerFeedback.new', {
                parent: 'consumerFeedback',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/consumerFeedback/consumerFeedback-dialog.html',
                        controller: 'ConsumerFeedbackDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    comment: null,
                                    likeOrDislike: null,
                                    notificationId: null,
                                    consumerId: null,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('consumerFeedback', null, { reload: true });
                    }, function() {
                        $state.go('consumerFeedback');
                    })
                }]
            })
            .state('consumerFeedback.edit', {
                parent: 'consumerFeedback',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/consumerFeedback/consumerFeedback-dialog.html',
                        controller: 'ConsumerFeedbackDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['ConsumerFeedback', function(ConsumerFeedback) {
                                return ConsumerFeedback.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('consumerFeedback', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            })
            .state('consumerFeedback.delete', {
                parent: 'consumerFeedback',
                url: '/{id}/delete',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/consumerFeedback/consumerFeedback-delete-dialog.html',
                        controller: 'ConsumerFeedbackDeleteController',
                        size: 'md',
                        resolve: {
                            entity: ['ConsumerFeedback', function(ConsumerFeedback) {
                                return ConsumerFeedback.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('consumerFeedback', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    }]);

'use strict';

angular.module('reachoutApp')
    .controller('ConsumerFeedbackController', ["$scope", "$state", "ConsumerFeedback", function ($scope, $state, ConsumerFeedback) {

        $scope.consumerFeedbacks = [];
        $scope.loadAll = function() {
            ConsumerFeedback.query(function(result) {
               $scope.consumerFeedbacks = result;
            });
        };
        $scope.loadAll();


        $scope.refresh = function () {
            $scope.loadAll();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.consumerFeedback = {
                comment: null,
                likeOrDislike: null,
                notificationId: null,
                consumerId: null,
                id: null
            };
        };
    }]);

'use strict';

angular.module('reachoutApp').controller('ConsumerFeedbackDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'ConsumerFeedback',
        function($scope, $stateParams, $uibModalInstance, entity, ConsumerFeedback) {

        $scope.consumerFeedback = entity;
        $scope.load = function(id) {
            ConsumerFeedback.get({id : id}, function(result) {
                $scope.consumerFeedback = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('reachoutApp:consumerFeedbackUpdate', result);
            $uibModalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.consumerFeedback.id != null) {
                ConsumerFeedback.update($scope.consumerFeedback, onSaveSuccess, onSaveError);
            } else {
                ConsumerFeedback.save($scope.consumerFeedback, onSaveSuccess, onSaveError);
            }
        };

        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
}]);

'use strict';

angular.module('reachoutApp')
	.controller('ConsumerFeedbackDeleteController', ["$scope", "$uibModalInstance", "entity", "ConsumerFeedback", function($scope, $uibModalInstance, entity, ConsumerFeedback) {

        $scope.consumerFeedback = entity;
        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.confirmDelete = function (id) {
            ConsumerFeedback.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };

    }]);

'use strict';

angular.module('reachoutApp')
    .controller('ConsumerFeedbackDetailController', ["$scope", "$rootScope", "$stateParams", "entity", "ConsumerFeedback", function ($scope, $rootScope, $stateParams, entity, ConsumerFeedback) {
        $scope.consumerFeedback = entity;
        $scope.load = function (id) {
            ConsumerFeedback.get({id: id}, function(result) {
                $scope.consumerFeedback = result;
            });
        };
        var unsubscribe = $rootScope.$on('reachoutApp:consumerFeedbackUpdate', function(event, result) {
            $scope.consumerFeedback = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }]);

'use strict';

angular.module('reachoutApp')
    .factory('ConsumerFeedback', ["$resource", "DateUtils", function ($resource, DateUtils) {
        return $resource('api/consumerFeedbacks/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }]);

'use strict';

angular.module('reachoutApp')
    .config(["$stateProvider", function ($stateProvider) {
        $stateProvider
            .state('consumerRegions', {
                parent: 'entity',
                url: '/consumerRegionss',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'reachoutApp.consumerRegions.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/consumerRegions/consumerRegionss.html',
                        controller: 'ConsumerRegionsController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('consumerRegions');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('consumerRegions.detail', {
                parent: 'entity',
                url: '/consumerRegions/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'reachoutApp.consumerRegions.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/consumerRegions/consumerRegions-detail.html',
                        controller: 'ConsumerRegionsDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('consumerRegions');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'ConsumerRegions', function($stateParams, ConsumerRegions) {
                        return ConsumerRegions.get({id : $stateParams.id});
                    }]
                }
            })
            .state('consumerRegions.new', {
                parent: 'consumerRegions',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/consumerRegions/consumerRegions-dialog.html',
                        controller: 'ConsumerRegionsDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    consumerId: null,
                                    region: null,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('consumerRegions', null, { reload: true });
                    }, function() {
                        $state.go('consumerRegions');
                    })
                }]
            })
            .state('consumerRegions.edit', {
                parent: 'consumerRegions',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/consumerRegions/consumerRegions-dialog.html',
                        controller: 'ConsumerRegionsDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['ConsumerRegions', function(ConsumerRegions) {
                                return ConsumerRegions.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('consumerRegions', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            })
            .state('consumerRegions.delete', {
                parent: 'consumerRegions',
                url: '/{id}/delete',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/consumerRegions/consumerRegions-delete-dialog.html',
                        controller: 'ConsumerRegionsDeleteController',
                        size: 'md',
                        resolve: {
                            entity: ['ConsumerRegions', function(ConsumerRegions) {
                                return ConsumerRegions.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('consumerRegions', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    }]);

'use strict';

angular.module('reachoutApp')
    .controller('ConsumerRegionsController', ["$scope", "$state", "ConsumerRegions", function ($scope, $state, ConsumerRegions) {

        $scope.consumerRegionss = [];
        $scope.loadAll = function() {
            ConsumerRegions.query(function(result) {
               $scope.consumerRegionss = result;
            });
        };
        $scope.loadAll();


        $scope.refresh = function () {
            $scope.loadAll();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.consumerRegions = {
                consumerId: null,
                region: null,
                id: null
            };
        };
    }]);

'use strict';

angular.module('reachoutApp').controller('ConsumerRegionsDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'ConsumerRegions',
        function($scope, $stateParams, $uibModalInstance, entity, ConsumerRegions) {

        $scope.consumerRegions = entity;
        $scope.load = function(id) {
            ConsumerRegions.get({id : id}, function(result) {
                $scope.consumerRegions = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('reachoutApp:consumerRegionsUpdate', result);
            $uibModalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.consumerRegions.id != null) {
                ConsumerRegions.update($scope.consumerRegions, onSaveSuccess, onSaveError);
            } else {
                ConsumerRegions.save($scope.consumerRegions, onSaveSuccess, onSaveError);
            }
        };

        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
}]);

'use strict';

angular.module('reachoutApp')
	.controller('ConsumerRegionsDeleteController', ["$scope", "$uibModalInstance", "entity", "ConsumerRegions", function($scope, $uibModalInstance, entity, ConsumerRegions) {

        $scope.consumerRegions = entity;
        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.confirmDelete = function (id) {
            ConsumerRegions.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };

    }]);

'use strict';

angular.module('reachoutApp')
    .controller('ConsumerRegionsDetailController', ["$scope", "$rootScope", "$stateParams", "entity", "ConsumerRegions", function ($scope, $rootScope, $stateParams, entity, ConsumerRegions) {
        $scope.consumerRegions = entity;
        $scope.load = function (id) {
            ConsumerRegions.get({id: id}, function(result) {
                $scope.consumerRegions = result;
            });
        };
        var unsubscribe = $rootScope.$on('reachoutApp:consumerRegionsUpdate', function(event, result) {
            $scope.consumerRegions = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }]);

'use strict';

angular.module('reachoutApp')
    .factory('ConsumerRegions', ["$resource", "DateUtils", function ($resource, DateUtils) {
        return $resource('api/consumerRegionss/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }]);

'use strict';

angular.module('reachoutApp')
    .config(["$stateProvider", function ($stateProvider) {
        $stateProvider
            .state('region', {
                parent: 'entity',
                url: '/regions',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'reachoutApp.region.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/region/regions.html',
                        controller: 'RegionController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('region');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('region.detail', {
                parent: 'entity',
                url: '/region/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'reachoutApp.region.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/region/region-detail.html',
                        controller: 'RegionDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('region');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'Region', function($stateParams, Region) {
                        return Region.get({id : $stateParams.id});
                    }]
                }
            })
            .state('region.new', {
                parent: 'region',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/region/region-dialog.html',
                        controller: 'RegionDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    name: null,
                                    location: null,
                                    created: null,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('region', null, { reload: true });
                    }, function() {
                        $state.go('region');
                    })
                }]
            })
            .state('region.edit', {
                parent: 'region',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/region/region-dialog.html',
                        controller: 'RegionDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Region', function(Region) {
                                return Region.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('region', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            })
            .state('region.delete', {
                parent: 'region',
                url: '/{id}/delete',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/region/region-delete-dialog.html',
                        controller: 'RegionDeleteController',
                        size: 'md',
                        resolve: {
                            entity: ['Region', function(Region) {
                                return Region.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('region', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    }]);

'use strict';

angular.module('reachoutApp')
    .controller('RegionController', ["$scope", "$state", "Region", function ($scope, $state, Region) {

        $scope.regions = [];
        $scope.loadAll = function() {
            Region.query(function(result) {
               $scope.regions = result;
            });
        };
        $scope.loadAll();


        $scope.refresh = function () {
            $scope.loadAll();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.region = {
                name: null,
                location: null,
                created: null,
                id: null
            };
        };
    }]);

'use strict';

angular.module('reachoutApp').controller('RegionDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'Region',
        function($scope, $stateParams, $uibModalInstance, entity, Region) {

        $scope.region = entity;
        $scope.load = function(id) {
            Region.get({id : id}, function(result) {
                $scope.region = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('reachoutApp:regionUpdate', result);
            $uibModalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.region.id != null) {
                Region.update($scope.region, onSaveSuccess, onSaveError);
            } else {
                Region.save($scope.region, onSaveSuccess, onSaveError);
            }
        };

        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.datePickerForCreated = {};

        $scope.datePickerForCreated.status = {
            opened: false
        };

        $scope.datePickerForCreatedOpen = function($event) {
            $scope.datePickerForCreated.status.opened = true;
        };
}]);

'use strict';

angular.module('reachoutApp')
	.controller('RegionDeleteController', ["$scope", "$uibModalInstance", "entity", "Region", function($scope, $uibModalInstance, entity, Region) {

        $scope.region = entity;
        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.confirmDelete = function (id) {
            Region.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };

    }]);

'use strict';

angular.module('reachoutApp')
    .controller('RegionDetailController', ["$scope", "$rootScope", "$stateParams", "entity", "Region", function ($scope, $rootScope, $stateParams, entity, Region) {
        $scope.region = entity;
        $scope.load = function (id) {
            Region.get({id: id}, function(result) {
                $scope.region = result;
            });
        };
        var unsubscribe = $rootScope.$on('reachoutApp:regionUpdate', function(event, result) {
            $scope.region = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }]);

'use strict';

angular.module('reachoutApp')
    .factory('Region', ["$resource", "DateUtils", function ($resource, DateUtils) {
        return $resource('api/regions/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.created = DateUtils.convertDateTimeFromServer(data.created);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }]);

'use strict';

angular.module('reachoutApp')
    .config(["$stateProvider", function ($stateProvider) {
        $stateProvider
            .state('notificationAcknowledgement', {
                parent: 'entity',
                url: '/notificationAcknowledgements',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'reachoutApp.notificationAcknowledgement.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/notificationAcknowledgement/notificationAcknowledgements.html',
                        controller: 'NotificationAcknowledgementController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('notificationAcknowledgement');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('notificationAcknowledgement.detail', {
                parent: 'entity',
                url: '/notificationAcknowledgement/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'reachoutApp.notificationAcknowledgement.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/notificationAcknowledgement/notificationAcknowledgement-detail.html',
                        controller: 'NotificationAcknowledgementDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('notificationAcknowledgement');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'NotificationAcknowledgement', function($stateParams, NotificationAcknowledgement) {
                        return NotificationAcknowledgement.get({id : $stateParams.id});
                    }]
                }
            })
            .state('notificationAcknowledgement.new', {
                parent: 'notificationAcknowledgement',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/notificationAcknowledgement/notificationAcknowledgement-dialog.html',
                        controller: 'NotificationAcknowledgementDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    read: null,
                                    consumerId: null,
                                    notificationId: null,
                                    delivered: null,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('notificationAcknowledgement', null, { reload: true });
                    }, function() {
                        $state.go('notificationAcknowledgement');
                    })
                }]
            })
            .state('notificationAcknowledgement.edit', {
                parent: 'notificationAcknowledgement',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/notificationAcknowledgement/notificationAcknowledgement-dialog.html',
                        controller: 'NotificationAcknowledgementDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['NotificationAcknowledgement', function(NotificationAcknowledgement) {
                                return NotificationAcknowledgement.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('notificationAcknowledgement', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            })
            .state('notificationAcknowledgement.delete', {
                parent: 'notificationAcknowledgement',
                url: '/{id}/delete',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/notificationAcknowledgement/notificationAcknowledgement-delete-dialog.html',
                        controller: 'NotificationAcknowledgementDeleteController',
                        size: 'md',
                        resolve: {
                            entity: ['NotificationAcknowledgement', function(NotificationAcknowledgement) {
                                return NotificationAcknowledgement.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('notificationAcknowledgement', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    }]);

'use strict';

angular.module('reachoutApp')
    .controller('NotificationAcknowledgementController', ["$scope", "$state", "NotificationAcknowledgement", function ($scope, $state, NotificationAcknowledgement) {

        $scope.notificationAcknowledgements = [];
        $scope.loadAll = function() {
            NotificationAcknowledgement.query(function(result) {
               $scope.notificationAcknowledgements = result;
            });
        };
        $scope.loadAll();


        $scope.refresh = function () {
            $scope.loadAll();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.notificationAcknowledgement = {
                read: null,
                consumerId: null,
                notificationId: null,
                delivered: null,
                id: null
            };
        };
    }]);

'use strict';

angular.module('reachoutApp').controller('NotificationAcknowledgementDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'NotificationAcknowledgement',
        function($scope, $stateParams, $uibModalInstance, entity, NotificationAcknowledgement) {

        $scope.notificationAcknowledgement = entity;
        $scope.load = function(id) {
            NotificationAcknowledgement.get({id : id}, function(result) {
                $scope.notificationAcknowledgement = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('reachoutApp:notificationAcknowledgementUpdate', result);
            $uibModalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.notificationAcknowledgement.id != null) {
                NotificationAcknowledgement.update($scope.notificationAcknowledgement, onSaveSuccess, onSaveError);
            } else {
                NotificationAcknowledgement.save($scope.notificationAcknowledgement, onSaveSuccess, onSaveError);
            }
        };

        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
}]);

'use strict';

angular.module('reachoutApp')
	.controller('NotificationAcknowledgementDeleteController', ["$scope", "$uibModalInstance", "entity", "NotificationAcknowledgement", function($scope, $uibModalInstance, entity, NotificationAcknowledgement) {

        $scope.notificationAcknowledgement = entity;
        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.confirmDelete = function (id) {
            NotificationAcknowledgement.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };

    }]);

'use strict';

angular.module('reachoutApp')
    .controller('NotificationAcknowledgementDetailController', ["$scope", "$rootScope", "$stateParams", "entity", "NotificationAcknowledgement", function ($scope, $rootScope, $stateParams, entity, NotificationAcknowledgement) {
        $scope.notificationAcknowledgement = entity;
        $scope.load = function (id) {
            NotificationAcknowledgement.get({id: id}, function(result) {
                $scope.notificationAcknowledgement = result;
            });
        };
        var unsubscribe = $rootScope.$on('reachoutApp:notificationAcknowledgementUpdate', function(event, result) {
            $scope.notificationAcknowledgement = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }]);

'use strict';

angular.module('reachoutApp')
    .factory('NotificationAcknowledgement', ["$resource", "DateUtils", function ($resource, DateUtils) {
        return $resource('api/notificationAcknowledgements/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }]);

'use strict';

angular.module('reachoutApp')
    .config(["$stateProvider", function ($stateProvider) {
        $stateProvider
            .state('privateMessage', {
                parent: 'entity',
                url: '/privateMessages',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'reachoutApp.privateMessage.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/privateMessage/privateMessages.html',
                        controller: 'PrivateMessageController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('privateMessage');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('privateMessage.detail', {
                parent: 'entity',
                url: '/privateMessage/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'reachoutApp.privateMessage.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/privateMessage/privateMessage-detail.html',
                        controller: 'PrivateMessageDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('privateMessage');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'PrivateMessage', function($stateParams, PrivateMessage) {
                        return PrivateMessage.get({id : $stateParams.id});
                    }]
                }
            })
            .state('privateMessage.new', {
                parent: 'privateMessage',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/privateMessage/privateMessage-dialog.html',
                        controller: 'PrivateMessageDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    message: null,
                                    read: null,
                                    delivered: null,
                                    created: null,
                                    notificationId: null,
                                    senderId: null,
                                    receiverId: null,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('privateMessage', null, { reload: true });
                    }, function() {
                        $state.go('privateMessage');
                    })
                }]
            })
            .state('privateMessage.edit', {
                parent: 'privateMessage',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/privateMessage/privateMessage-dialog.html',
                        controller: 'PrivateMessageDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['PrivateMessage', function(PrivateMessage) {
                                return PrivateMessage.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('privateMessage', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            })
            .state('privateMessage.delete', {
                parent: 'privateMessage',
                url: '/{id}/delete',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/privateMessage/privateMessage-delete-dialog.html',
                        controller: 'PrivateMessageDeleteController',
                        size: 'md',
                        resolve: {
                            entity: ['PrivateMessage', function(PrivateMessage) {
                                return PrivateMessage.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('privateMessage', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    }]);

'use strict';

angular.module('reachoutApp')
    .controller('PrivateMessageController', ["$scope", "$state", "PrivateMessage", function ($scope, $state, PrivateMessage) {

        $scope.privateMessages = [];
        $scope.loadAll = function() {
            PrivateMessage.query(function(result) {
               $scope.privateMessages = result;
            });
        };
        $scope.loadAll();


        $scope.refresh = function () {
            $scope.loadAll();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.privateMessage = {
                message: null,
                read: null,
                delivered: null,
                created: null,
                notificationId: null,
                senderId: null,
                receiverId: null,
                id: null
            };
        };
    }]);

'use strict';

angular.module('reachoutApp').controller('PrivateMessageDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'PrivateMessage',
        function($scope, $stateParams, $uibModalInstance, entity, PrivateMessage) {

        $scope.privateMessage = entity;
        $scope.load = function(id) {
            PrivateMessage.get({id : id}, function(result) {
                $scope.privateMessage = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('reachoutApp:privateMessageUpdate', result);
            $uibModalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.privateMessage.id != null) {
                PrivateMessage.update($scope.privateMessage, onSaveSuccess, onSaveError);
            } else {
                PrivateMessage.save($scope.privateMessage, onSaveSuccess, onSaveError);
            }
        };

        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.datePickerForCreated = {};

        $scope.datePickerForCreated.status = {
            opened: false
        };

        $scope.datePickerForCreatedOpen = function($event) {
            $scope.datePickerForCreated.status.opened = true;
        };
}]);

'use strict';

angular.module('reachoutApp')
	.controller('PrivateMessageDeleteController', ["$scope", "$uibModalInstance", "entity", "PrivateMessage", function($scope, $uibModalInstance, entity, PrivateMessage) {

        $scope.privateMessage = entity;
        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.confirmDelete = function (id) {
            PrivateMessage.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };

    }]);

'use strict';

angular.module('reachoutApp')
    .controller('PrivateMessageDetailController', ["$scope", "$rootScope", "$stateParams", "entity", "PrivateMessage", function ($scope, $rootScope, $stateParams, entity, PrivateMessage) {
        $scope.privateMessage = entity;
        $scope.load = function (id) {
            PrivateMessage.get({id: id}, function(result) {
                $scope.privateMessage = result;
            });
        };
        var unsubscribe = $rootScope.$on('reachoutApp:privateMessageUpdate', function(event, result) {
            $scope.privateMessage = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }]);

'use strict';

angular.module('reachoutApp')
    .factory('PrivateMessage', ["$resource", "DateUtils", function ($resource, DateUtils) {
        return $resource('api/privateMessages/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.created = DateUtils.convertDateTimeFromServer(data.created);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }]);

'use strict';

angular.module('reachoutApp')
    .config(["$stateProvider", function ($stateProvider) {
        $stateProvider
            .state('deviceInfo', {
                parent: 'entity',
                url: '/deviceInfos',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'reachoutApp.deviceInfo.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/deviceInfo/deviceInfos.html',
                        controller: 'DeviceInfoController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('deviceInfo');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('deviceInfo.detail', {
                parent: 'entity',
                url: '/deviceInfo/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'reachoutApp.deviceInfo.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/deviceInfo/deviceInfo-detail.html',
                        controller: 'DeviceInfoDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('deviceInfo');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'DeviceInfo', function($stateParams, DeviceInfo) {
                        return DeviceInfo.get({id : $stateParams.id});
                    }]
                }
            })
            .state('deviceInfo.new', {
                parent: 'deviceInfo',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/deviceInfo/deviceInfo-dialog.html',
                        controller: 'DeviceInfoDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    device: null,
                                    sdk: null,
                                    model: null,
                                    product: null,
                                    consumerId: null,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('deviceInfo', null, { reload: true });
                    }, function() {
                        $state.go('deviceInfo');
                    })
                }]
            })
            .state('deviceInfo.edit', {
                parent: 'deviceInfo',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/deviceInfo/deviceInfo-dialog.html',
                        controller: 'DeviceInfoDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['DeviceInfo', function(DeviceInfo) {
                                return DeviceInfo.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('deviceInfo', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            })
            .state('deviceInfo.delete', {
                parent: 'deviceInfo',
                url: '/{id}/delete',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/deviceInfo/deviceInfo-delete-dialog.html',
                        controller: 'DeviceInfoDeleteController',
                        size: 'md',
                        resolve: {
                            entity: ['DeviceInfo', function(DeviceInfo) {
                                return DeviceInfo.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('deviceInfo', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    }]);

'use strict';

angular.module('reachoutApp')
    .controller('DeviceInfoController', ["$scope", "$state", "DeviceInfo", function ($scope, $state, DeviceInfo) {

        $scope.deviceInfos = [];
        $scope.loadAll = function() {
            DeviceInfo.query(function(result) {
               $scope.deviceInfos = result;
            });
        };
        $scope.loadAll();


        $scope.refresh = function () {
            $scope.loadAll();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.deviceInfo = {
                device: null,
                sdk: null,
                model: null,
                product: null,
                consumerId: null,
                id: null
            };
        };
    }]);

'use strict';

angular.module('reachoutApp').controller('DeviceInfoDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'DeviceInfo',
        function($scope, $stateParams, $uibModalInstance, entity, DeviceInfo) {

        $scope.deviceInfo = entity;
        $scope.load = function(id) {
            DeviceInfo.get({id : id}, function(result) {
                $scope.deviceInfo = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('reachoutApp:deviceInfoUpdate', result);
            $uibModalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.deviceInfo.id != null) {
                DeviceInfo.update($scope.deviceInfo, onSaveSuccess, onSaveError);
            } else {
                DeviceInfo.save($scope.deviceInfo, onSaveSuccess, onSaveError);
            }
        };

        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
}]);

'use strict';

angular.module('reachoutApp')
	.controller('DeviceInfoDeleteController', ["$scope", "$uibModalInstance", "entity", "DeviceInfo", function($scope, $uibModalInstance, entity, DeviceInfo) {

        $scope.deviceInfo = entity;
        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.confirmDelete = function (id) {
            DeviceInfo.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };

    }]);

'use strict';

angular.module('reachoutApp')
    .controller('DeviceInfoDetailController', ["$scope", "$rootScope", "$stateParams", "entity", "DeviceInfo", function ($scope, $rootScope, $stateParams, entity, DeviceInfo) {
        $scope.deviceInfo = entity;
        $scope.load = function (id) {
            DeviceInfo.get({id: id}, function(result) {
                $scope.deviceInfo = result;
            });
        };
        var unsubscribe = $rootScope.$on('reachoutApp:deviceInfoUpdate', function(event, result) {
            $scope.deviceInfo = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }]);

'use strict';

angular.module('reachoutApp')
    .factory('DeviceInfo', ["$resource", "DateUtils", function ($resource, DateUtils) {
        return $resource('api/deviceInfos/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }]);

'use strict';

angular.module('reachoutApp')
    .config(["$stateProvider", function ($stateProvider) {
        $stateProvider
            .state('consumer', {
                parent: 'entity',
                url: '/consumers',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'reachoutApp.consumer.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/consumer/consumers.html',
                        controller: 'ConsumerController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('consumer');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('consumer.detail', {
                parent: 'entity',
                url: '/consumer/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'reachoutApp.consumer.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/consumer/consumer-detail.html',
                        controller: 'ConsumerDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('consumer');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'Consumer', function($stateParams, Consumer) {
                        return Consumer.get({id : $stateParams.id});
                    }]
                }
            })
            .state('consumer.new', {
                parent: 'consumer',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/consumer/consumer-dialog.html',
                        controller: 'ConsumerDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    mobile: null,
                                    email: null,
                                    status: null,
                                    otp: null,
                                    otpCount: null,
                                    created: null,
                                    updated: null,
                                    name: null,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('consumer', null, { reload: true });
                    }, function() {
                        $state.go('consumer');
                    })
                }]
            })
            .state('consumer.edit', {
                parent: 'consumer',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/consumer/consumer-dialog.html',
                        controller: 'ConsumerDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Consumer', function(Consumer) {
                                return Consumer.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('consumer', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            })
            .state('consumer.delete', {
                parent: 'consumer',
                url: '/{id}/delete',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/consumer/consumer-delete-dialog.html',
                        controller: 'ConsumerDeleteController',
                        size: 'md',
                        resolve: {
                            entity: ['Consumer', function(Consumer) {
                                return Consumer.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('consumer', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    }]);

'use strict';

angular.module('reachoutApp')
    .controller('ConsumerController', ["$scope", "$http", "$state", "Consumer", function ($scope, $http, $state, Consumer) {

        $scope.consumers = [];
        $scope.search = "";
        $scope.loadAll = function() {
            Consumer.query(function(result) {
               $scope.consumers = result;
            });
        };
        $scope.loadAll();


        $scope.refresh = function () {
            $scope.loadAll();
            $scope.clear();
        };

        $scope.active = function(id,active){
        	var url = "api/consumers/userActivation"
        	active = !active
        	$scope.data = {
        			id: id,
        			active: active
        	};
        	 $http.post(url, $scope.data).success(function(data){
        		 $scope.consumers = data;
                 $scope.loadAll();
        	 });
        };
        
        $scope.clear = function () {
            $scope.consumer = {
                mobile: null,
                email: null,
                status: null,
                otp: null,
                otpCount: null,
                created: null,
                updated: null,
                name: null,
                id: null
            };
        };
    }]);

'use strict';

angular.module('reachoutApp').controller('ConsumerDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'Consumer',
        function($scope, $stateParams, $uibModalInstance, entity, Consumer) {

        $scope.consumer = entity;
        $scope.load = function(id) {
            Consumer.get({id : id}, function(result) {
                $scope.consumer = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('reachoutApp:consumerUpdate', result);
            $uibModalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.consumer.id != null) {
                Consumer.update($scope.consumer, onSaveSuccess, onSaveError);
            } else {
                Consumer.save($scope.consumer, onSaveSuccess, onSaveError);
            }
        };

        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.datePickerForCreated = {};

        $scope.datePickerForCreated.status = {
            opened: false
        };

        $scope.datePickerForCreatedOpen = function($event) {
            $scope.datePickerForCreated.status.opened = true;
        };
        $scope.datePickerForUpdated = {};

        $scope.datePickerForUpdated.status = {
            opened: false
        };

        $scope.datePickerForUpdatedOpen = function($event) {
            $scope.datePickerForUpdated.status.opened = true;
        };
}]);

'use strict';

angular.module('reachoutApp')
	.controller('ConsumerDeleteController', ["$scope", "$uibModalInstance", "entity", "Consumer", function($scope, $uibModalInstance, entity, Consumer) {

        $scope.consumer = entity;
        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.confirmDelete = function (id) {
            Consumer.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };

    }]);

'use strict';

angular.module('reachoutApp')
    .controller('ConsumerDetailController', ["$scope", "$rootScope", "$stateParams", "entity", "Consumer", function ($scope, $rootScope, $stateParams, entity, Consumer) {
        $scope.consumer = entity;
        $scope.load = function (id) {
            Consumer.get({id: id}, function(result) {
                $scope.consumer = result;
            });
        };
        var unsubscribe = $rootScope.$on('reachoutApp:consumerUpdate', function(event, result) {
            $scope.consumer = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }]);

'use strict';

angular.module('reachoutApp')
    .factory('Consumer', ["$resource", "DateUtils", function ($resource, DateUtils) {
        return $resource('api/consumers/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.created = DateUtils.convertDateTimeFromServer(data.created);
                    data.updated = DateUtils.convertDateTimeFromServer(data.updated);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }]);

angular.module('reachoutApp').run(['$templateCache', function($templateCache) {
  'use strict';

  $templateCache.put('scripts/app/account/activate/activate.html',
    "<div> <div class=row> <div class=\"col-md-8 col-md-offset-2\"> <h1 translate=activate.title>Activation</h1> <div class=\"alert alert-success\" ng-show=success translate=activate.messages.success> <strong>Your user has been activated.</strong> Please <a class=alert-link href=#/login>sign in</a>. </div> <div class=\"alert alert-danger\" ng-show=error translate=activate.messages.error> <strong>Your user could not be activated.</strong> Please use the registration form to sign up. </div> </div> </div> </div>"
  );


  $templateCache.put('scripts/app/account/login/login.html',
    "<div> <div class=row> <div class=\"col-md-4 col-md-offset-4\"> <h1 translate=login.title>Sign in</h1> <div class=\"alert alert-danger\" ng-show=authenticationError translate=login.messages.error.authentication> <strong>Failed to sign in!</strong> Please check your credentials and try again. </div> </div> <div class=\"col-md-4 col-md-offset-4\"> <form class=form role=form ng-submit=login($event)> <div class=form-group> <label for=username translate=global.form.username>Login</label> <input class=form-control id=username placeholder=\"{{'global.form.username.placeholder' | translate}}\" ng-model=username> </div> <div class=form-group> <label for=password translate=login.form.password>Password</label> <input type=password class=form-control id=password placeholder=\"{{'login.form.password.placeholder' | translate}}\" ng-model=password> </div> <button type=submit class=\"btn btn-primary\" translate=login.form.button>Sign in</button> </form> <p></p> <div class=\"alert alert-warning\"> <a class=alert-link href=#/reset/request translate=login.password.forgot>Did you forget your password?</a> </div> <div class=\"alert alert-warning\" translate=global.messages.info.register> You don't have an account yet? <a class=alert-link href=#/register>Register a new account</a> </div> </div> </div> </div>"
  );


  $templateCache.put('scripts/app/account/password/password.html',
    "<div> <div class=row> <div class=\"col-md-8 col-md-offset-2\"> <h2 translate=password.title translate-values=\"{username: '{{account.login}}'}\">Password for [<b>{{account.login}}</b>]</h2> <div class=\"alert alert-success\" ng-show=success translate=password.messages.success> <strong>Password changed!</strong> </div> <div class=\"alert alert-danger\" ng-show=error translate=password.messages.error> <strong>An error has occurred!</strong> The password could not be changed. </div> <div class=\"alert alert-danger\" ng-show=doNotMatch translate=global.messages.error.dontmatch> The password and its confirmation do not match! </div> <form name=form role=form novalidate ng-submit=changePassword() show-validation> <div class=form-group> <label class=control-label for=password translate=global.form.newpassword>New password</label> <input type=password class=form-control id=password name=password placeholder=\"{{'global.form.newpassword.placeholder' | translate}}\" ng-model=password ng-minlength=5 ng-maxlength=50 required> <div ng-show=\"form.password.$dirty && form.password.$invalid\"> <p class=help-block ng-show=form.password.$error.required translate=global.messages.validate.newpassword.required> Your password is required. </p> <p class=help-block ng-show=form.password.$error.minlength translate=global.messages.validate.newpassword.minlength> Your password is required to be at least 5 characters. </p> <p class=help-block ng-show=form.password.$error.maxlength translate=global.messages.validate.newpassword.maxlength> Your password cannot be longer than 50 characters. </p> </div> <password-strength-bar password-to-check=password></password-strength-bar> </div> <div class=form-group> <label class=control-label for=confirmPassword translate=global.form.confirmpassword>New password confirmation</label> <input type=password class=form-control id=confirmPassword name=confirmPassword placeholder=\"{{'global.form.confirmpassword.placeholder' | translate}}\" ng-model=confirmPassword ng-minlength=5 ng-maxlength=50 required> <div ng-show=\"form.confirmPassword.$dirty && form.confirmPassword.$invalid\"> <p class=help-block ng-show=form.confirmPassword.$error.required translate=global.messages.validate.confirmpassword.required> Your confirmation password is required. </p> <p class=help-block ng-show=form.confirmPassword.$error.minlength translate=global.messages.validate.confirmpassword.minlength> Your confirmation password is required to be at least 5 characters. </p> <p class=help-block ng-show=form.confirmPassword.$error.maxlength translate=global.messages.validate.confirmpassword.maxlength> Your confirmation password cannot be longer than 50 characters. </p> </div> </div> <button type=submit ng-disabled=form.$invalid class=\"btn btn-primary\" translate=password.form.button>Save</button> </form> </div> </div> </div>"
  );


  $templateCache.put('scripts/app/account/register/register.html',
    "<div> <div class=row> <div class=\"col-md-8 col-md-offset-2\"> <h1 translate=register.title>Registration</h1> <div class=\"alert alert-success\" ng-show=success translate=register.messages.success> <strong>Registration saved!</strong> Please check your email for confirmation. </div> <div class=\"alert alert-danger\" ng-show=error translate=register.messages.error.fail> <strong>Registration failed!</strong> Please try again later. </div> <div class=\"alert alert-danger\" ng-show=errorUserExists translate=register.messages.error.userexists> <strong>Login name already registered!</strong> Please choose another one. </div> <div class=\"alert alert-danger\" ng-show=errorEmailExists translate=register.messages.error.emailexists> <strong>E-mail is already in use!</strong> Please choose another one. </div> <div class=\"alert alert-danger\" ng-show=doNotMatch translate=global.messages.error.dontmatch> The password and its confirmation do not match! </div> </div> <div class=\"col-md-8 col-md-offset-2\"> <form ng-show=!success name=form role=form novalidate ng-submit=register() show-validation> <div class=form-group> <label class=control-label for=login translate=global.form.username>Username</label> <input class=form-control id=login name=login placeholder=\"{{'global.form.username.placeholder' | translate}}\" ng-model=registerAccount.login ng-minlength=1 ng-maxlength=50 ng-pattern=\"/^[a-z0-9]*$/\" required> <div ng-show=\"form.login.$dirty && form.login.$invalid\"> <p class=help-block ng-show=form.login.$error.required translate=register.messages.validate.login.required> Your username is required. </p> <p class=help-block ng-show=form.login.$error.minlength translate=register.messages.validate.login.minlength> Your username is required to be at least 1 character. </p> <p class=help-block ng-show=form.login.$error.maxlength translate=register.messages.validate.login.maxlength> Your username cannot be longer than 50 characters. </p> <p class=help-block ng-show=form.login.$error.pattern translate=register.messages.validate.login.pattern> Your username can only contain lower-case letters and digits. </p> </div> </div> <div class=form-group> <label class=control-label for=email translate=global.form.email>E-mail</label> <input type=email class=form-control id=email name=email placeholder=\"{{'global.form.email.placeholder' | translate}}\" ng-model=registerAccount.email ng-minlength=5 ng-maxlength=100 required> <div ng-show=\"form.email.$dirty && form.email.$invalid\"> <p class=help-block ng-show=form.email.$error.required translate=global.messages.validate.email.required> Your e-mail is required. </p> <p class=help-block ng-show=form.email.$error.email translate=global.messages.validate.email.invalid> Your e-mail is invalid. </p> <p class=help-block ng-show=form.email.$error.minlength translate=global.messages.validate.email.minlength> Your e-mail is required to be at least 5 characters. </p> <p class=help-block ng-show=form.email.$error.maxlength translate=global.messages.validate.email.maxlength> Your e-mail cannot be longer than 100 characters. </p> </div> </div> <div class=form-group> <label class=control-label for=password translate=global.form.newpassword>New password</label> <input type=password class=form-control id=password name=password placeholder=\"{{'global.form.newpassword.placeholder' | translate}}\" ng-model=registerAccount.password ng-minlength=5 ng-maxlength=50 required> <div ng-show=\"form.password.$dirty && form.password.$invalid\"> <p class=help-block ng-show=form.password.$error.required translate=global.messages.validate.newpassword.required> Your password is required. </p> <p class=help-block ng-show=form.password.$error.minlength translate=global.messages.validate.newpassword.minlength> Your password is required to be at least 5 characters. </p> <p class=help-block ng-show=form.password.$error.maxlength translate=global.messages.validate.newpassword.maxlength> Your password cannot be longer than 50 characters. </p> </div> <password-strength-bar password-to-check=registerAccount.password></password-strength-bar> </div> <div class=form-group> <label class=control-label for=confirmPassword translate=global.form.confirmpassword>New password confirmation</label> <input type=password class=form-control id=confirmPassword name=confirmPassword placeholder=\"{{'global.form.confirmpassword.placeholder' | translate}}\" ng-model=confirmPassword ng-minlength=5 ng-maxlength=50 required> <div ng-show=\"form.confirmPassword.$dirty && form.confirmPassword.$invalid\"> <p class=help-block ng-show=form.confirmPassword.$error.required translate=global.messages.validate.confirmpassword.required> Your confirmation password is required. </p> <p class=help-block ng-show=form.confirmPassword.$error.minlength translate=global.messages.validate.confirmpassword.minlength> Your confirmation password is required to be at least 5 characters. </p> <p class=help-block ng-show=form.confirmPassword.$error.maxlength translate=global.messages.validate.confirmpassword.maxlength> Your confirmation password cannot be longer than 50 characters. </p> </div> </div> <button type=submit ng-disabled=form.$invalid class=\"btn btn-primary\" translate=register.form.button>Register</button> </form> <p></p> <div class=\"alert alert-warning\" translate=global.messages.info.authenticated> If you want to <a class=alert-link href=#/login>sign in</a>, you can try the default accounts:<br/>- Administrator (login=\"admin\" and password=\"admin\") <br/>- User (login=\"user\" and password=\"user\"). </div> </div> </div> </div>"
  );


  $templateCache.put('scripts/app/account/reset/finish/reset.finish.html',
    "<div> <div class=row> <div class=\"col-md-4 col-md-offset-4\"> <h1 translate=reset.finish.title>Reset password</h1> <div class=\"alert alert-danger\" translate=reset.finish.messages.keymissing ng-show=keyMissing> <strong>The password reset key is missing.</strong> </div> <div class=\"alert alert-warning\" ng-hide=\"success || keyMissing\"> <p translate=reset.finish.messages.info>Choose a new password</p> </div> <div class=\"alert alert-danger\" ng-show=error> <p translate=reset.finish.messages.error>Your password couldn't be reset. Remember a password request is only valid for 24 hours.</p> </div> <div class=\"alert alert-success\" ng-show=success> <p translate=reset.finish.messages.success><strong>Your password has been reset.</strong> Please <a class=alert-link href=#/login>sign in</a>.</p> </div> <div class=\"alert alert-danger\" ng-show=doNotMatch translate=global.messages.error.dontmatch> The password and its confirmation do not match! </div> <div ng-hide=keyMissing> <form ng-show=!success name=form role=form novalidate ng-submit=finishReset() show-validation> <div class=form-group> <label class=control-label for=password translate=global.form.newpassword>New password</label> <input type=password class=form-control id=password name=password placeholder=\"{{'global.form.newpassword.placeholder' | translate}}\" ng-model=resetAccount.password ng-minlength=5 ng-maxlength=50 required> <div ng-show=\"form.password.$dirty && form.password.$invalid\"> <p class=help-block ng-show=form.password.$error.required translate=global.messages.validate.newpassword.required> Your password is required. </p> <p class=help-block ng-show=form.password.$error.minlength translate=global.messages.validate.newpassword.minlength> Your password is required to be at least 5 characters. </p> <p class=help-block ng-show=form.password.$error.maxlength translate=global.messages.validate.newpassword.maxlength> Your password cannot be longer than 50 characters. </p> </div> <password-strength-bar password-to-check=resetAccount.password></password-strength-bar> </div> <div class=form-group> <label class=control-label for=confirmPassword translate=global.form.confirmpassword>New password confirmation</label> <input type=password class=form-control id=confirmPassword name=confirmPassword placeholder=\"{{'global.form.confirmpassword.placeholder' | translate}}\" ng-model=confirmPassword ng-minlength=5 ng-maxlength=50 required> <div ng-show=\"form.confirmPassword.$dirty && form.confirmPassword.$invalid\"> <p class=help-block ng-show=form.confirmPassword.$error.required translate=global.messages.validate.confirmpassword.required> Your password confirmation is required. </p> <p class=help-block ng-show=form.confirmPassword.$error.minlength translate=global.messages.validate.confirmpassword.minlength> Your password confirmation is required to be at least 5 characters. </p> <p class=help-block ng-show=form.confirmPassword.$error.maxlength translate=global.messages.validate.confirmpassword.maxlength> Your password confirmation cannot be longer than 50 characters. </p> </div> </div> <button type=submit ng-disabled=form.$invalid class=\"btn btn-primary\" translate=reset.finish.form.button>Reset Password</button> </form> </div> </div> </div> </div>"
  );


  $templateCache.put('scripts/app/account/reset/request/reset.request.html',
    "<div> <div class=row> <div class=\"col-md-8 col-md-offset-2\"> <h1 translate=reset.request.title>Reset your password</h1> <div class=\"alert alert-danger\" translate=reset.request.messages.notfound ng-show=errorEmailNotExists> <strong>E-Mail address isn't registered!</strong> Please check and try again. </div> <div class=\"alert alert-warning\" ng-hide=success> <p translate=reset.request.messages.info>Enter the e-mail address you used to register.</p> </div> <div class=\"alert alert-success\" ng-show=\"success == 'OK'\"> <p translate=reset.request.messages.success>Check your e-mails for details on how to reset your password.</p> </div> <form ng-show=!success name=form role=form novalidate ng-submit=requestReset() show-validation> <div class=form-group> <label class=control-label for=email translate=global.form.email>E-mail</label> <input type=email class=form-control id=email name=email placeholder=\"{{'global.form.email.placeholder' | translate}}\" ng-model=resetAccount.email ng-minlength=5 ng-maxlength=100 required> <div ng-show=\"form.email.$dirty && form.email.$invalid\"> <p class=help-block ng-show=form.email.$error.required translate=global.messages.validate.email.required> Your e-mail is required. </p> <p class=help-block ng-show=form.email.$error.email translate=global.messages.validate.email.invalid> Your e-mail is invalid. </p> <p class=help-block ng-show=form.email.$error.minlength translate=global.messages.validate.email.minlength> Your e-mail is required to be at least 5 characters. </p> <p class=help-block ng-show=form.email.$error.maxlength translate=global.messages.validate.email.maxlength> Your e-mail cannot be longer than 100 characters. </p> </div> </div> <button type=submit ng-disabled=form.$invalid class=\"btn btn-primary\" translate=reset.request.form.button>Register</button> </form> </div> </div> </div>"
  );


  $templateCache.put('scripts/app/account/settings/settings.html',
    "<div> <div class=row> <div class=\"col-md-8 col-md-offset-2\"> <h2 translate=settings.title translate-values=\"{username: '{{settingsAccount.login}}'}\">User settings for [<b>{{settingsAccount.login}}</b>]</h2> <div class=\"alert alert-success\" ng-show=success translate=settings.messages.success> <strong>Settings saved!</strong> </div> <div class=\"alert alert-danger\" ng-show=errorEmailExists translate=settings.messages.error.emailexists> <strong>E-mail is already in use!</strong> Please choose another one. </div> <div class=\"alert alert-danger\" ng-show=error translate=settings.messages.error.fail> <strong>An error has occurred!</strong> Settings could not be saved. </div> <form name=form role=form novalidate ng-submit=save() show-validation> <div class=form-group> <label class=control-label for=firstName translate=settings.form.firstname>First Name</label> <input class=form-control id=firstName name=firstName placeholder=\"{{'settings.form.firstname.placeholder' | translate}}\" ng-model=settingsAccount.firstName ng-minlength=1 ng-maxlength=50 required maxlength=50> <div ng-show=\"form.firstName.$dirty && form.firstName.$invalid\"> <p class=help-block ng-show=form.firstName.$error.required translate=settings.messages.validate.firstname.required> Your first name is required. </p> <p class=help-block ng-show=form.firstName.$error.minlength translate=settings.messages.validate.firstname.minlength> Your first name is required to be at least 1 character. </p> <p class=help-block ng-show=form.firstName.$error.maxlength translate=settings.messages.validate.firstname.maxlength> Your first name cannot be longer than 50 characters. </p> </div> </div> <div class=form-group> <label class=control-label for=lastName translate=settings.form.lastname>Last Name</label> <input class=form-control id=lastName name=lastName placeholder=\"{{'settings.form.lastname.placeholder' | translate}}\" ng-model=settingsAccount.lastName ng-minlength=1 ng-maxlength=50 required maxlength=50> <div ng-show=\"form.lastName.$dirty && form.lastName.$invalid\"> <p class=help-block ng-show=form.lastName.$error.required translate=settings.messages.validate.lastname.required> Your last name is required. </p> <p class=help-block ng-show=form.lastName.$error.minlength translate=settings.messages.validate.lastname.minlength> Your last name is required to be at least 1 character. </p> <p class=help-block ng-show=form.lastName.$error.maxlength translate=settings.messages.validate.lastname.maxlength> Your last name cannot be longer than 50 characters. </p> </div> </div> <div class=form-group> <label class=control-label for=email translate=global.form.email>E-mail</label> <input type=email class=form-control id=email name=email placeholder=\"{{'global.form.email.placeholder' | translate}}\" ng-model=settingsAccount.email ng-minlength=5 ng-maxlength=100 required maxlength=100> <div ng-show=\"form.email.$dirty && form.email.$invalid\"> <p class=help-block ng-show=form.email.$error.required translate=global.messages.validate.email.required> Your e-mail is required. </p> <p class=help-block ng-show=form.email.$error.email translate=global.messages.validate.email.invalid> Your e-mail is invalid. </p> <p class=help-block ng-show=form.email.$error.minlength translate=global.messages.validate.email.minlength> Your e-mail is required to be at least 5 characters. </p> <p class=help-block ng-show=form.email.$error.maxlength translate=global.messages.validate.email.maxlength> Your e-mail cannot be longer than 100 characters. </p> </div> </div> <div class=form-group> <label for=langKey translate=settings.form.language>Language</label> <select id=langKey name=langKey class=form-control ng-model=settingsAccount.langKey ng-controller=LanguageController ng-options=\"code as (code | findLanguageFromKey) for code in languages\"></select> </div> <button type=submit ng-disabled=form.$invalid class=\"btn btn-primary\" translate=settings.form.button>Save</button> </form> </div> </div> </div>"
  );


  $templateCache.put('scripts/app/admin/audits/audits.html',
    "<div> <h2 translate=audits.title>Audits</h2> <div class=row> <div class=col-md-5> <h4 translate=audits.filter.title>Filter by date</h4> <p class=input-group> <span class=input-group-addon translate=audits.filter.from>from</span> <input type=date class=\"input-sm form-control\" name=start ng-model=fromDate ng-change=onChangeDate() required/> <span class=input-group-addon translate=audits.filter.to>to</span> <input type=date class=\"input-sm form-control\" name=end ng-model=toDate ng-change=onChangeDate() required/> </p> </div> </div> <table class=\"table table-condensed table-striped table-bordered table-responsive\"> <thead> <tr> <th ng-click=\"predicate = 'timestamp'; reverse=!reverse\"><span translate=audits.table.header.date>Date</span></th> <th ng-click=\"predicate = 'principal'; reverse=!reverse\"><span translate=audits.table.header.principal>User</span></th> <th ng-click=\"predicate = 'type'; reverse=!reverse\"><span translate=audits.table.header.status>State</span></th> <th ng-click=\"predicate = 'data.message'; reverse=!reverse\"><span translate=audits.table.header.data>Extra data</span></th> </tr> </thead> <tr ng-repeat=\"audit in audits | filter:filter | orderBy:predicate:reverse\" ng-hide=audit.filtered> <td><span>{{audit.timestamp| date:'medium'}}</span></td> <td><small>{{audit.principal}}</small></td> <td>{{audit.type}}</td> <td> <span ng-show=audit.data.message>{{audit.data.message}}</span> <span ng-show=audit.data.remoteAddress><span translate=audits.table.data.remoteAddress>Remote Address</span> {{audit.data.remoteAddress}}</span> </td> </tr> </table> </div>"
  );


  $templateCache.put('scripts/app/admin/configuration/configuration.html',
    "<div> <h2 translate=configuration.title>configuration</h2> <span translate=configuration.filter>Filter (by prefix)</span> <input ng-model=filter class=form-control> <table class=\"table table-condensed table-striped table-bordered table-responsive\" style=table-layout:fixed> <thead> <tr> <th ng-click=\"predicate = 'prefix'; reverse=!reverse\" class=col-sm-4><span translate=configuration.table.prefix>Prefix</span></th> <th translate=configuration.table.properties class=col-sm-8>Properties</th> </tr> </thead> <tr ng-repeat=\"entry in configuration | filter:filter | orderBy:predicate:reverse\"> <td><span>{{entry.prefix}}</span></td> <td> <div class=row ng-repeat=\"(key, value) in entry.properties\"> <div class=col-md-4>{{key}}</div> <div class=col-md-8><span class=\"pull-right label label-info\" style=\"white-space: normal;word-break:break-all\">{{value}}</span></div> </div> </td> </tr> </table> </div>"
  );


  $templateCache.put('scripts/app/admin/docs/docs.html',
    "<iframe src=swagger-ui/index.html frameborder=0 marginheight=0 marginwidth=0 width=100% height=900 scrolling=auto target=_top></iframe>"
  );


  $templateCache.put('scripts/app/admin/health/health.html',
    "<div> <h2 translate=health.title>Health Checks</h2> <p> <button type=button class=\"btn btn-primary\" ng-click=refresh()><span class=\"glyphicon glyphicon-refresh\"></span>&nbsp;<span translate=health.refresh.button>Refresh</span> </button> </p> <table id=healthCheck class=\"table table-striped\"> <thead> <tr> <th class=col-md-7 translate=health.table.service>Service Name</th> <th class=\"col-md-2 text-center\" translate=health.table.status>Status</th> <th class=\"col-md-2 text-center\" translate=health.details.details>Details</th> </tr> </thead> <tbody> <tr ng-repeat=\"health in healthData\"> <td>{{'health.indicator.' + baseName(health.name) | translate}} {{subSystemName(health.name)}}</td> <td class=text-center> <span class=label ng-class=getLabelClass(health.status)> {{'health.status.' + health.status | translate}} </span> </td> <td class=text-center> <a class=hand ng-click=showHealth(health) ng-show=\"health.details || health.error\"> <i class=\"glyphicon glyphicon-eye-open\"></i> </a> </td> </tr> </tbody> </table> </div>"
  );


  $templateCache.put('scripts/app/admin/health/health.modal.html',
    "<div class=modal-header> <button aria-label=Close data-dismiss=modal class=close type=button ng-click=cancel()><span aria-hidden=true>&times;</span> </button> <h4 class=modal-title id=showHealthLabel> {{'health.indicator.' + baseName(currentHealth.name) | translate}} {{subSystemName(currentHealth.name)}} </h4> </div> <div class=\"modal-body pad\"> <div ng-show=currentHealth.details> <h4 translate=health.details.properties>Properties</h4> <table class=\"table table-striped\"> <thead> <tr> <th class=\"col-md-6 text-left\" translate=health.details.name>Name</th> <th class=\"col-md-6 text-left\" translate=health.details.value>Value</th> </tr> </thead> <tbody> <tr ng-repeat=\"(k,v) in currentHealth.details\"> <td class=\"col-md-6 text-left\">{{k}}</td> <td class=\"col-md-6 text-left\">{{v}}</td> </tr> </tbody> </table> </div> <div ng-show=currentHealth.error> <h4 translate=health.details.error>Error</h4> <pre>{{currentHealth.error}}</pre> </div> </div> <div class=modal-footer> <button data-dismiss=modal class=\"btn btn-default pull-left\" type=button ng-click=cancel()>Done</button> </div>"
  );


  $templateCache.put('scripts/app/admin/logs/logs.html',
    "<div> <h2 translate=logs.title>Logs</h2> <p translate=logs.nbloggers translate-values=\"{total: '{{ loggers.length }}'}\">There are {{ loggers.length }} loggers.</p> <span translate=logs.filter>Filter</span> <input ng-model=filter class=form-control> <table class=\"table table-condensed table-striped table-bordered table-responsive\"> <thead> <tr title=\"click to order\"> <th ng-click=\"predicate = 'name'; reverse=!reverse\"><span translate=logs.table.name>Name</span></th> <th ng-click=\"predicate = 'level'; reverse=!reverse\"><span translate=logs.table.level>Level</span></th> </tr> </thead> <tr ng-repeat=\"logger in loggers | filter:filter | orderBy:predicate:reverse\"> <td><small>{{logger.name | characters:140}}</small></td> <td> <button ng-click=\"changeLevel(logger.name, 'TRACE')\" ng-class=\"(logger.level=='TRACE') ? 'btn-danger' : 'btn-default'\" class=\"btn btn-default btn-xs\">TRACE</button> <button ng-click=\"changeLevel(logger.name, 'DEBUG')\" ng-class=\"(logger.level=='DEBUG') ? 'btn-warning' : 'btn-default'\" class=\"btn btn-default btn-xs\">DEBUG</button> <button ng-click=\"changeLevel(logger.name, 'INFO')\" ng-class=\"(logger.level=='INFO') ? 'btn-info' : 'btn-default'\" class=\"btn btn-default btn-xs\">INFO</button> <button ng-click=\"changeLevel(logger.name, 'WARN')\" ng-class=\"(logger.level=='WARN') ? 'btn-success' : 'btn-default'\" class=\"btn btn-default btn-xs\">WARN</button> <button ng-click=\"changeLevel(logger.name, 'ERROR')\" ng-class=\"(logger.level=='ERROR') ? 'btn-primary' : 'btn-default'\" class=\"btn btn-default btn-xs\">ERROR</button> </td> </tr> </table> </div>"
  );


  $templateCache.put('scripts/app/admin/metrics/metrics.html',
    "<div> <h2 translate=metrics.title>Application Metrics</h2> <p> <button type=button class=\"btn btn-primary\" ng-click=refresh()><span class=\"glyphicon glyphicon-refresh\"></span>&nbsp;<span translate=metrics.refresh.button>Refresh</span></button> </p> <h3 translate=metrics.jvm.title>JVM Metrics</h3> <div class=row ng-hide=updatingMetrics> <div class=col-md-4> <b translate=metrics.jvm.memory.title>Memory</b> <p><span translate=metrics.jvm.memory.total>Total Memory</span> ({{metrics.gauges['jvm.memory.total.used'].value / 1000000 | number:0}}M / {{metrics.gauges['jvm.memory.total.max'].value / 1000000 | number:0}}M)</p> <uib-progressbar min=0 max=\"metrics.gauges['jvm.memory.total.max'].value\" value=\"metrics.gauges['jvm.memory.total.used'].value\" class=\"progress-striped active\" type=success> <span>{{metrics.gauges['jvm.memory.total.used'].value * 100 / metrics.gauges['jvm.memory.total.max'].value | number:0}}%</span> </uib-progressbar> <p><span translate=metrics.jvm.memory.heap>Heap Memory</span> ({{metrics.gauges['jvm.memory.heap.used'].value / 1000000 | number:0}}M / {{metrics.gauges['jvm.memory.heap.max'].value / 1000000 | number:0}}M)</p> <uib-progressbar min=0 max=\"metrics.gauges['jvm.memory.heap.max'].value\" value=\"metrics.gauges['jvm.memory.heap.used'].value\" class=\"progress-striped active\" type=success> <span>{{metrics.gauges['jvm.memory.heap.used'].value * 100 / metrics.gauges['jvm.memory.heap.max'].value | number:0}}%</span> </uib-progressbar> <p><span translate=metrics.jvm.memory.nonheap>Non-Heap Memory</span> ({{metrics.gauges['jvm.memory.non-heap.used'].value / 1000000 | number:0}}M / {{metrics.gauges['jvm.memory.non-heap.committed'].value / 1000000 | number:0}}M)</p> <uib-progressbar min=0 max=\"metrics.gauges['jvm.memory.non-heap.committed'].value\" value=\"metrics.gauges['jvm.memory.non-heap.used'].value\" class=\"progress-striped active\" type=success> <span>{{metrics.gauges['jvm.memory.non-heap.used'].value * 100 / metrics.gauges['jvm.memory.non-heap.committed'].value | number:0}}%</span> </uib-progressbar> </div> <div class=col-md-4> <b translate=metrics.jvm.threads.title>Threads</b> (Total: {{metrics.gauges['jvm.threads.count'].value}}) <a class=hand ng-click=refreshThreadDumpData() data-toggle=modal data-target=#threadDump><i class=\"glyphicon glyphicon-eye-open\"></i></a> <p><span translate=metrics.jvm.threads.runnable>Runnable</span> {{metrics.gauges['jvm.threads.runnable.count'].value}}</p> <uib-progressbar min=0 value=\"metrics.gauges['jvm.threads.runnable.count'].value\" max=\"metrics.gauges['jvm.threads.count'].value\" class=\"progress-striped active\" type=success> <span>{{metrics.gauges['jvm.threads.runnable.count'].value * 100 / metrics.gauges['jvm.threads.count'].value | number:0}}%</span> </uib-progressbar> <p><span translate=metrics.jvm.threads.timedwaiting>Timed Waiting</span> ({{metrics.gauges['jvm.threads.timed_waiting.count'].value}})</p> <uib-progressbar min=0 value=\"metrics.gauges['jvm.threads.timed_waiting.count'].value\" max=\"metrics.gauges['jvm.threads.count'].value\" class=\"progress-striped active\" type=warning> <span>{{metrics.gauges['jvm.threads.timed_waiting.count'].value * 100 / metrics.gauges['jvm.threads.count'].value | number:0}}%</span> </uib-progressbar> <p><span translate=metrics.jvm.threads.waiting>Waiting</span> ({{metrics.gauges['jvm.threads.waiting.count'].value}})</p> <uib-progressbar min=0 value=\"metrics.gauges['jvm.threads.waiting.count'].value\" max=\"metrics.gauges['jvm.threads.count'].value\" class=\"progress-striped active\" type=warning> <span>{{metrics.gauges['jvm.threads.waiting.count'].value * 100 / metrics.gauges['jvm.threads.count'].value | number:0}}%</span> </uib-progressbar> <p><span translate=metrics.jvm.threads.blocked>Blocked</span> ({{metrics.gauges['jvm.threads.blocked.count'].value}})</p> <uib-progressbar min=0 value=\"metrics.gauges['jvm.threads.blocked.count'].value\" max=\"metrics.gauges['jvm.threads.count'].value\" class=\"progress-striped active\" type=success> <span>{{metrics.gauges['jvm.threads.blocked.count'].value * 100 / metrics.gauges['jvm.threads.count'].value | number:0}}%</span> </uib-progressbar> </div> <div class=col-md-4> <b translate=metrics.jvm.gc.title>Garbage collections</b> <div class=row> <div class=col-md-9 translate=metrics.jvm.gc.marksweepcount>Mark Sweep count</div> <div class=\"col-md-3 text-right\">{{metrics.gauges['jvm.garbage.PS-MarkSweep.count'].value}}</div> </div> <div class=row> <div class=col-md-9 translate=metrics.jvm.gc.marksweeptime>Mark Sweep time</div> <div class=\"col-md-3 text-right\">{{metrics.gauges['jvm.garbage.PS-MarkSweep.time'].value}}ms</div> </div> <div class=row> <div class=col-md-9 translate=metrics.jvm.gc.scavengecount>Scavenge count</div> <div class=\"col-md-3 text-right\">{{metrics.gauges['jvm.garbage.PS-Scavenge.count'].value}}</div> </div> <div class=row> <div class=col-md-9 translate=metrics.jvm.gc.scavengetime>Scavenge time</div> <div class=\"col-md-3 text-right\">{{metrics.gauges['jvm.garbage.PS-Scavenge.time'].value}}ms</div> </div> </div> </div> <div class=\"well well-lg\" ng-show=updatingMetrics translate=metrics.updating>Updating...</div> <h3 translate=metrics.jvm.http.title>HTTP requests (events per second)</h3> <p><span translate=metrics.jvm.http.active>Active requests</span> <b>{{metrics.counters['com.codahale.metrics.servlet.InstrumentedFilter.activeRequests'].count | number:0}}</b> - <span translate=metrics.jvm.http.total>Total requests</span> <b>{{metrics.timers['com.codahale.metrics.servlet.InstrumentedFilter.requests'].count | number:0}}</b></p> <div class=table-responsive> <table class=\"table table-striped\"> <thead> <tr> <th translate=metrics.jvm.http.table.code>Code</th> <th translate=metrics.jvm.http.table.count>Count</th> <th class=text-right translate=metrics.jvm.http.table.mean>Mean</th> <th class=text-right><span translate=metrics.jvm.http.table.average>Average</span> (1 min)</th> <th class=text-right><span translate=metrics.jvm.http.table.average>Average</span> (5 min)</th> <th class=text-right><span translate=metrics.jvm.http.table.average>Average</span> (15 min)</th> </tr> </thead> <tbody> <tr> <td translate=metrics.jvm.http.code.ok>OK</td> <td> <uib-progressbar min=0 max=\"metrics.timers['com.codahale.metrics.servlet.InstrumentedFilter.requests'].count\" value=\"metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.ok'].count\" class=\"progress-striped active\" type=success> <span>{{metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.ok'].count}}</span> </uib-progressbar> </td> <td class=text-right> {{metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.ok'].mean_rate | number:2}} </td> <td class=text-right>{{metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.ok'].m1_rate | number:2}} </td> <td class=text-right>{{metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.ok'].m5_rate | number:2}} </td> <td class=text-right> {{metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.ok'].m15_rate | number:2}} </td> </tr> <tr> <td translate=metrics.jvm.http.code.notfound>Not Found</td> <td> <uib-progressbar min=0 max=\"metrics.timers['com.codahale.metrics.servlet.InstrumentedFilter.requests'].count\" value=\"metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.notFound'].count\" class=\"progress-striped active\" type=success> <span>{{metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.notFound'].count}}</span> </uib-progressbar> </td> <td class=text-right> {{metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.notFound'].mean_rate | number:2}} </td> <td class=text-right> {{metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.notFound'].m1_rate | number:2}} </td> <td class=text-right> {{metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.notFound'].m5_rate | number:2}} </td> <td class=text-right> {{metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.notFound'].m15_rate | number:2}} </td> </tr> <tr> <td translate=metrics.jvm.http.code.servererror>Server error</td> <td> <uib-progressbar min=0 max=\"metrics.timers['com.codahale.metrics.servlet.InstrumentedFilter.requests'].count\" value=\"metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.serverError'].count\" class=\"progress-striped active\" type=success> <span>{{metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.serverError'].count}}</span> </uib-progressbar> </td> <td class=text-right> {{metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.serverError'].mean_rate | number:2}} </td> <td class=text-right> {{metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.serverError'].m1_rate | number:2}} </td> <td class=text-right> {{metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.serverError'].m5_rate | number:2}} </td> <td class=text-right> {{metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.serverError'].m15_rate | number:2}} </td> </tr> </tbody> </table> </div> <h3 translate=metrics.servicesstats.title>Services statistics (time in millisecond)</h3> <div class=table-responsive> <table class=\"table table-striped\"> <thead> <tr> <th translate=metrics.servicesstats.table.name>Service name</th> <th class=text-right translate=metrics.servicesstats.table.count>Count</th> <th class=text-right translate=metrics.servicesstats.table.mean>Mean</th> <th class=text-right translate=metrics.servicesstats.table.min>Min</th> <th class=text-right translate=metrics.servicesstats.table.p50>p50</th> <th class=text-right translate=metrics.servicesstats.table.p75>p75</th> <th class=text-right translate=metrics.servicesstats.table.p95>p95</th> <th class=text-right translate=metrics.servicesstats.table.p99>p99</th> <th class=text-right translate=metrics.servicesstats.table.max>Max</th> </tr> </thead> <tbody> <tr ng-repeat=\"(k, v) in servicesStats\"> <td>{{k}}</td> <td class=text-right>{{v.count}}</td> <td class=text-right>{{v.mean * 1000 | number:0}}</td> <td class=text-right>{{v.min * 1000 | number:0}}</td> <td class=text-right>{{v.p50 * 1000 | number:0}}</td> <td class=text-right>{{v.p75 * 1000 | number:0}}</td> <td class=text-right>{{v.p95 * 1000 | number:0}}</td> <td class=text-right>{{v.p99 * 1000 | number:0}}</td> <td class=text-right>{{v.max * 1000 | number:0}}</td> </tr> </tbody> </table> </div> <h3 translate=metrics.datasource.title ng-show=\"metrics.gauges['HikariPool-0.pool.TotalConnections'].value > 0\">DataSource statistics (time in millisecond)</h3> <div class=table-responsive ng-show=\"metrics.gauges['HikariPool-0.pool.TotalConnections'].value > 0\"> <table class=\"table table-striped\"> <thead> <tr> <th><span translate=metrics.datasource.usage>Usage</span> ({{metrics.gauges['HikariPool-0.pool.ActiveConnections'].value}} / {{metrics.gauges['HikariPool-0.pool.TotalConnections'].value}})</th> <th class=text-right translate=metrics.datasource.count>Count</th> <th class=text-right translate=metrics.datasource.mean>Mean</th> <th class=text-right translate=metrics.datasource.min>Min</th> <th class=text-right translate=metrics.datasource.p50>p50</th> <th class=text-right translate=metrics.datasource.p75>p75</th> <th class=text-right translate=metrics.datasource.p95>p95</th> <th class=text-right translate=metrics.datasource.p99>p99</th> <th class=text-right translate=metrics.datasource.max>Max</th> </tr> </thead> <tbody> <tr> <td> <div class=\"progress progress-striped\"> <uib-progressbar min=0 max=\"metrics.gauges['HikariPool-0.pool.TotalConnections'].value\" value=\"metrics.gauges['HikariPool-0.pool.ActiveConnections'].value\" class=\"progress-striped active\" type=success> <span>{{metrics.gauges['HikariPool-0.pool.ActiveConnections'].value * 100 / metrics.gauges['HikariPool-0.pool.TotalConnections'].value | number:0}}%</span> </uib-progressbar> </div> </td> <td class=text-right>{{metrics.histograms['HikariPool-0.pool.Usage'].count}}</td> <td class=text-right>{{metrics.histograms['HikariPool-0.pool.Usage'].mean | number:2}}</td> <td class=text-right>{{metrics.histograms['HikariPool-0.pool.Usage'].min | number:2}}</td> <td class=text-right>{{metrics.histograms['HikariPool-0.pool.Usage'].p50 | number:2}}</td> <td class=text-right>{{metrics.histograms['HikariPool-0.pool.Usage'].p75 | number:2}}</td> <td class=text-right>{{metrics.histograms['HikariPool-0.pool.Usage'].p95 | number:2}}</td> <td class=text-right>{{metrics.histograms['HikariPool-0.pool.Usage'].p99 | number:2}}</td> <td class=text-right>{{metrics.histograms['HikariPool-0.pool.Usage'].max | number:2}}</td> </tr> </tbody> </table> </div> </div>"
  );


  $templateCache.put('scripts/app/admin/metrics/metrics.modal.html',
    "<!-- Modal used to display the threads dump --> <div class=modal-header> <button type=button class=close ng-click=cancel()>&times;</button> <h4 class=modal-title translate=metrics.jvm.threads.dump.title>Threads dump</h4> </div> <div class=\"modal-body pad\"> <span class=\"label label-primary\" ng-click=\"threadDumpFilter = {}\">All&nbsp;<span class=badge>{{threadDumpAll}}</span></span>&nbsp; <span class=\"label label-success\" ng-click=\"threadDumpFilter = {threadState: 'RUNNABLE'}\">Runnable&nbsp;<span class=badge>{{threadDumpRunnable}}</span></span>&nbsp; <span class=\"label label-info\" ng-click=\"threadDumpFilter = {threadState: 'WAITING'}\">Waiting&nbsp;<span class=badge>{{threadDumpWaiting}}</span></span>&nbsp; <span class=\"label label-warning\" ng-click=\"threadDumpFilter = {threadState: 'TIMED_WAITING'}\">Timed Waiting&nbsp;<span class=badge>{{threadDumpTimedWaiting}}</span></span>&nbsp; <span class=\"label label-danger\" ng-click=\"threadDumpFilter = {threadState: 'BLOCKED'}\">Blocked&nbsp;<span class=badge>{{threadDumpBlocked}}</span></span>&nbsp; <div class=voffset2>&nbsp;</div> Filter <input ng-model=threadDumpFilter class=form-control> <div class=\"row pad\" ng-repeat=\"(k, v) in threadDump | filter:threadDumpFilter\"> <h5><span class=label ng-class=getLabelClass(v.threadState)>{{v.threadState}}</span>&nbsp;{{v.threadName}} (ID {{v.threadId}}) <a ng-click=\"show = !show\"> <span ng-show=!show translate=metrics.jvm.threads.dump.show>Show StackTrace</span> <span ng-show=show translate=metrics.jvm.threads.dump.hide>Hide StackTrace</span> </a> </h5> <div class=well ng-show=show> <div ng-repeat=\"(stK, stV) in v.stackTrace\"> {{stV.className}}.{{stV.methodName}}({{stV.fileName}}:{{stV.lineNumber}}) <span class=voffset1></span> </div> </div> <table class=\"table table-condensed\"> <thead> <tr> <th class=text-right translate=metrics.jvm.threads.dump.blockedtime>Blocked Time</th> <th class=text-right translate=metrics.jvm.threads.dump.blockedcount>Blocked Count</th> <th class=text-right translate=metrics.jvm.threads.dump.waitedtime>Waited Time</th> <th class=text-right translate=metrics.jvm.threads.dump.waitedcount>Waited Count</th> <th translate=metrics.jvm.threads.dump.lockname>Lock Name</th> </tr> </thead> <tbody> <tr> <td>{{v.blockedTime}}</td> <td>{{v.blockedCount}}</td> <td>{{v.waitedTime}}</td> <td>{{v.waitedCount}}</td> <td>{{v.lockName}}</td> </tr> </tbody> </table> </div> </div> <div class=modal-footer> <button type=button class=\"btn btn-default pull-left\" data-dismiss=modal ng-click=cancel()>Done</button> </div>"
  );


  $templateCache.put('scripts/app/admin/tracker/tracker.html',
    "<div> <h2 translate=tracker.title>Real-time user activities</h2> <div class=table-responsive> <table class=\"table table-striped\"> <thead> <tr> <th translate=tracker.table.userlogin>User</th> <th translate=tracker.table.ipaddress>IP Address</th> <th translate=tracker.table.page>Current page</th> <th translate=tracker.table.time>Time</th> <th></th> </tr> </thead> <tbody> <tr ng-repeat=\"activity in activities\"> <td>{{activity.userLogin}}</td> <td>{{activity.ipAddress}}</td> <td>{{activity.page}}</td> <td>{{activity.time}}</td> </tr> </tbody> </table> </div> </div>"
  );


  $templateCache.put('scripts/app/admin/user-management/user-management-delete-dialog.html',
    "<form name=deleteForm ng-submit=confirmDelete(user.login)> <div class=modal-header> <button type=button class=close data-dismiss=modal aria-hidden=true ng-click=clear()>&times;</button> <h4 class=modal-title translate=entity.delete.title>Confirm delete operation</h4> </div> <div class=modal-body> <p translate=user-management.delete.question translate-values=\"{login: '{{user.login}}'}\">Are you sure you want to delete this User?</p> </div> <div class=modal-footer> <button type=button class=\"btn btn-default\" data-dismiss=modal ng-click=clear()> <span class=\"glyphicon glyphicon-ban-circle\"></span>&nbsp;<span translate=entity.action.cancel>Cancel</span> </button> <button type=submit ng-disabled=deleteForm.$invalid class=\"btn btn-danger\"> <span class=\"glyphicon glyphicon-remove-circle\"></span>&nbsp;<span translate=entity.action.delete>Delete</span> </button> </div> </form>"
  );


  $templateCache.put('scripts/app/admin/user-management/user-management-detail.html',
    "<div> <h2><span translate=user-management.detail.title>User</span> \"{{user.login}}\"</h2> <div class=table-responsive> <table class=\"table table-striped\"> <thead> <tr> <th translate=entity.detail.field>Field</th> <th translate=entity.detail.value>Value</th> </tr> </thead> <tbody> <tr> <td> <span translate=user-management.login>Login</span> </td> <td> <input class=\"input-sm form-control\" value={{user.login}} readonly> </td> </tr> <tr> <td> <span translate=user-management.firstName>FirstName</span> </td> <td> <input class=\"input-sm form-control\" value={{user.firstName}} readonly> </td> </tr> <tr> <td> <span translate=user-management.lastName>LastName</span> </td> <td> <input class=\"input-sm form-control\" value={{user.lastName}} readonly> </td> </tr> <tr> <td> <span translate=user-management.email>Email</span> </td> <td> <input class=\"input-sm form-control\" value={{user.email}} readonly> </td> </tr> <tr> <td> <span translate=user-management.activated>Activated</span> </td> <td> <input class=\"input-sm form-control\" value={{user.activated}} readonly> </td> </tr> <tr> <td> <span translate=user-management.langKey>LangKey</span> </td> <td> <input class=\"input-sm form-control\" value={{user.langKey}} readonly> </td> </tr> <tr> <td> <span translate=user-management.createdBy>CreatedBy</span> </td> <td> <input class=\"input-sm form-control\" value={{user.createdBy}} readonly> </td> </tr> <tr> <td> <span translate=user-management.createdDate>CreatedDate</span> </td> <td> <input class=\"input-sm form-control\" value=\"{{user.createdDate | date:'dd/MM/yy HH:mm' }}\" readonly> </td> </tr> <tr> <td> <span translate=user-management.lastModifiedBy>LastModifiedBy</span> </td> <td> <input class=\"input-sm form-control\" value={{user.lastModifiedBy}} readonly> </td> </tr> <tr> <td> <span translate=user-management.lastModifiedDate>LastCreatedDate</span> </td> <td> <input class=\"input-sm form-control\" value=\"{{user.lastModifiedDate | date:'dd/MM/yy HH:mm'}}\" readonly> </td> </tr> <tr> <td> <span translate=user-management.profiles>Profiles</span> </td> <td> <ul class=list-unstyled> <li ng-repeat=\"authority in user.authorities\"><span>{{authority}}</span></li> </ul> </td> </tr> </tbody> </table> </div> <button type=submit ui-sref=user-management class=\"btn btn-info\"> <span class=\"glyphicon glyphicon-arrow-left\"></span>&nbsp;<span translate=entity.action.back> Back</span> </button> </div>"
  );


  $templateCache.put('scripts/app/admin/user-management/user-management-dialog.html',
    "<form name=editForm role=form novalidate ng-submit=save() show-validation> <div class=modal-header> <button type=button class=close data-dismiss=modal aria-hidden=true ng-click=clear()>&times;</button> <h4 class=modal-title id=myUserLabel translate=user-management.home.createOrEditLabel> Create or edit a User</h4> </div> <div class=modal-body> <jh-alert-error></jh-alert-error> <div class=form-group> <label translate=global.field.id>ID</label> <input class=form-control name=id ng-model=user.id readonly> </div> <div class=form-group> <label class=control-label translate=user-management.login>Login</label> <input class=form-control name=login ng-model=user.login ng-required=\"user.id == null\" ng-maxlength=50> <div ng-show=editForm.login.$invalid> <p class=help-block ng-show=editForm.login.$error.required translate=entity.validation.required> This field is required. </p> <p class=help-block ng-show=editForm.login.$error.maxlength translate=entity.validation.maxlength translate-value-max=50> This field cannot be longer than 50 characters. </p> </div> </div> <div class=form-group> <label class=control-label translate=user-management.firstName>FirstName</label> <input class=form-control name=firstName ng-model=user.firstName ng-maxlength=50> <div ng-show=editForm.firstName.$invalid> <p class=help-block ng-show=editForm.firstName.$error.maxlength translate=entity.validation.maxlength translate-value-max=50> This field cannot be longer than 50 characters. </p> </div> </div> <div class=form-group> <label translate=user-management.lastName>LastName</label> <input class=form-control name=lastName ng-model=user.lastName ng-maxlength=50> <div ng-show=editForm.lastName.$invalid> <p class=help-block ng-show=editForm.lastName.$error.maxlength translate=entity.validation.maxlength translate-value-max=50> This field cannot be longer than 50 characters. </p> </div> </div> <div class=form-group> <label class=control-label translate=user-management.email>Email</label> <input class=form-control name=email ng-model=user.email required ng-maxlength=100> <div ng-show=editForm.email.$invalid> <p class=help-block ng-show=editForm.email.$error.required translate=entity.validation.required> This field is required. </p> <p class=help-block ng-show=editForm.email.$error.maxlength translate=entity.validation.maxlength translate-value-max=100> This field cannot be longer than 100 characters. </p> </div> </div> <div class=form-group> <label for=activated> <input type=checkbox id=activated ng-model=user.activated> <span translate=user-management.activated>Activated</span> </label> </div> <div class=form-group> <label translate=user-management.langKey>LangKey</label> <select class=form-control name=langKey ng-model=user.langKey ng-options=\"language as language for language in languages track by language\"> </select> </div> <div class=form-group> <label translate=user-management.profiles>Profiles</label> <select class=form-control multiple name=authority ng-model=user.authorities ng-options=\"authority for authority in authorities\"> </select> </div> </div> <div class=modal-footer> <button type=button class=\"btn btn-default\" data-dismiss=modal ng-click=clear()> <span class=\"glyphicon glyphicon-ban-circle\"></span>&nbsp;<span translate=entity.action.cancel>Cancel</span> </button> <button type=submit ng-disabled=\"editForm.$invalid || isSaving\" class=\"btn btn-primary\"> <span class=\"glyphicon glyphicon-save\"></span>&nbsp;<span translate=entity.action.save>Save</span> </button> </div> </form>"
  );


  $templateCache.put('scripts/app/admin/user-management/user-management.html',
    "<div> <h2 translate=user-management.home.title>Users</h2> <jh-alert></jh-alert> <div class=container> <div class=row> <div class=col-md-4> <button class=\"btn btn-primary\" ui-sref=user-management.new> <span class=\"glyphicon glyphicon-flash\"></span> <span translate=user-management.home.createLabel>Create a new User</span> </button> </div> </div> </div> <div class=table-responsive> <table class=\"table table-striped\"> <thead> <tr> <th translate=global.field.id>ID</th> <th translate=user-management.login>Login</th> <th translate=user-management.email>Email</th> <th></th> <th translate=user-management.langKey>LangKey</th> <th translate=user-management.profiles>Profiles</th> <th translate=user-management.createdDate>CreatedDate</th> <th translate=user-management.lastModifiedBy>LastModifiedBy</th> <th translate=user-management.lastModifiedDate>LastModifiedDate</th> <th></th> </tr> </thead> <tbody> <tr ng-repeat=\"user in users track by user.id\"> <td><a ui-sref=user-management-detail({login:user.login})>{{user.id}}</a></td> <td>{{user.login}}</td> <td>{{user.email}}</td> <td> <span class=\"label label-danger\" ng-click=\"setActive(user, true)\" ng-show=!user.activated translate=user-management.deactivated style=\"cursor: pointer\">Desactivated</span> <span class=\"label label-success\" ng-click=\"setActive(user, false)\" ng-show=user.activated translate=user-management.activated style=\"cursor: pointer\">Activated</span> </td> <td>{{user.langKey}}</td> <td> <div ng-repeat=\"authority in user.authorities\"> <span class=\"label label-info\">{{ authority }}</span> </div> </td> <td>{{user.createdDate | date:'dd/MM/yy HH:mm'}}</td> <td>{{user.lastModifiedBy}}</td> <td>{{user.lastModifiedDate | date:'dd/MM/yy HH:mm'}}</td> <td class=text-right> <button type=submit ui-sref=user-management-detail({login:user.login}) class=\"btn btn-info btn-sm\"> <span class=\"glyphicon glyphicon-eye-open\"></span> </button> <button type=submit ui-sref=user-management.edit({login:user.login}) class=\"btn btn-primary btn-sm\"> <span class=\"glyphicon glyphicon-pencil\"></span> </button> <button type=submit ui-sref=user-management.delete({login:user.login}) class=\"btn btn-danger btn-sm\"> <span class=\"glyphicon glyphicon-remove-circle\"></span> </button> </td> </tr> </tbody> </table> </div> <div class=text-center> <uib-pagination class=pagination-sm total-items=totalItems ng-model=page ng-change=loadAll()></uib-pagination> </div> </div>"
  );


  $templateCache.put('scripts/app/entities/cloudinary/cloudinary-delete-dialog.html',
    "<form name=deleteForm ng-submit=confirmDelete(cloudinary.id)> <div class=modal-header> <button type=button class=close data-dismiss=modal aria-hidden=true ng-click=clear()>&times;</button> <h4 class=modal-title translate=entity.delete.title>Confirm delete operation</h4> </div> <div class=modal-body> <p translate=reachoutApp.cloudinary.delete.question translate-values=\"{id: '{{cloudinary.id}}'}\">Are you sure you want to delete this Cloudinary?</p> </div> <div class=modal-footer> <button type=button class=\"btn btn-default\" data-dismiss=modal ng-click=clear()> <span class=\"glyphicon glyphicon-ban-circle\"></span>&nbsp;<span translate=entity.action.cancel>Cancel</span> </button> <button type=submit ng-disabled=deleteForm.$invalid class=\"btn btn-danger\"> <span class=\"glyphicon glyphicon-remove-circle\"></span>&nbsp;<span translate=entity.action.delete>Delete</span> </button> </div> </form>"
  );


  $templateCache.put('scripts/app/entities/cloudinary/cloudinary-detail.html',
    "<div> <h2><span translate=reachoutApp.cloudinary.detail.title>Cloudinary</span> {{cloudinary.id}}</h2> <div class=table-responsive> <table class=\"table table-striped\"> <thead> <tr> <th translate=entity.detail.field>Field</th> <th translate=entity.detail.value>Value</th> </tr> </thead> <tbody> <tr> <td> <span translate=reachoutApp.cloudinary.consumerId>ConsumerId</span> </td> <td> <span class=form-control-static>{{cloudinary.consumerId}}</span> </td> </tr> <tr> <td> <span translate=reachoutApp.cloudinary.signature>Signature</span> </td> <td> <span class=form-control-static>{{cloudinary.signature}}</span> </td> </tr> <tr> <td> <span translate=reachoutApp.cloudinary.format>Format</span> </td> <td> <span class=form-control-static>{{cloudinary.format}}</span> </td> </tr> <tr> <td> <span translate=reachoutApp.cloudinary.resourceType>ResourceType</span> </td> <td> <span class=form-control-static>{{cloudinary.resourceType}}</span> </td> </tr> <tr> <td> <span translate=reachoutApp.cloudinary.secureUrl>SecureUrl</span> </td> <td> <span class=form-control-static>{{cloudinary.secureUrl}}</span> </td> </tr> <tr> <td> <span translate=reachoutApp.cloudinary.created>Created</span> </td> <td> <span class=form-control-static>{{cloudinary.created | date:'medium'}}</span> </td> </tr> <tr> <td> <span translate=reachoutApp.cloudinary.type>Type</span> </td> <td> <span class=form-control-static>{{cloudinary.type}}</span> </td> </tr> <tr> <td> <span translate=reachoutApp.cloudinary.version>Version</span> </td> <td> <span class=form-control-static>{{cloudinary.version}}</span> </td> </tr> <tr> <td> <span translate=reachoutApp.cloudinary.url>Url</span> </td> <td> <span class=form-control-static>{{cloudinary.url}}</span> </td> </tr> <tr> <td> <span translate=reachoutApp.cloudinary.publicId>PublicId</span> </td> <td> <span class=form-control-static>{{cloudinary.publicId}}</span> </td> </tr> <tr> <td> <span translate=reachoutApp.cloudinary.tags>Tags</span> </td> <td> <span class=form-control-static>{{cloudinary.tags}}</span> </td> </tr> <tr> <td> <span translate=reachoutApp.cloudinary.orginalFileName>OrginalFileName</span> </td> <td> <span class=form-control-static>{{cloudinary.orginalFileName}}</span> </td> </tr> <tr> <td> <span translate=reachoutApp.cloudinary.bytes>Bytes</span> </td> <td> <span class=form-control-static>{{cloudinary.bytes}}</span> </td> </tr> <tr> <td> <span translate=reachoutApp.cloudinary.width>Width</span> </td> <td> <span class=form-control-static>{{cloudinary.width}}</span> </td> </tr> <tr> <td> <span translate=reachoutApp.cloudinary.eTag>ETag</span> </td> <td> <span class=form-control-static>{{cloudinary.eTag}}</span> </td> </tr> <tr> <td> <span translate=reachoutApp.cloudinary.height>Height</span> </td> <td> <span class=form-control-static>{{cloudinary.height}}</span> </td> </tr> </tbody> </table> </div> <button type=submit onclick=window.history.back() class=\"btn btn-info\"> <span class=\"glyphicon glyphicon-arrow-left\"></span>&nbsp;<span translate=entity.action.back> Back</span> </button> </div>"
  );


  $templateCache.put('scripts/app/entities/cloudinary/cloudinary-dialog.html',
    "<form name=editForm role=form novalidate ng-submit=save()> <div class=modal-header> <button type=button class=close data-dismiss=modal aria-hidden=true ng-click=clear()>&times;</button> <h4 class=modal-title id=myCloudinaryLabel translate=reachoutApp.cloudinary.home.createOrEditLabel>Create or edit a Cloudinary</h4> </div> <div class=modal-body> <jh-alert-error></jh-alert-error> <div class=form-group> <label for=id translate=global.field.id>ID</label> <input class=form-control id=id name=id ng-model=cloudinary.id readonly/> </div> <div class=form-group> <label class=control-label translate=reachoutApp.cloudinary.consumerId for=field_consumerId>ConsumerId</label> <input class=form-control name=consumerId id=field_consumerId ng-model=\"cloudinary.consumerId\"/> </div> <div class=form-group> <label class=control-label translate=reachoutApp.cloudinary.signature for=field_signature>Signature</label> <input class=form-control name=signature id=field_signature ng-model=\"cloudinary.signature\"/> </div> <div class=form-group> <label class=control-label translate=reachoutApp.cloudinary.format for=field_format>Format</label> <input class=form-control name=format id=field_format ng-model=\"cloudinary.format\"/> </div> <div class=form-group> <label class=control-label translate=reachoutApp.cloudinary.resourceType for=field_resourceType>ResourceType</label> <input class=form-control name=resourceType id=field_resourceType ng-model=\"cloudinary.resourceType\"/> </div> <div class=form-group> <label class=control-label translate=reachoutApp.cloudinary.secureUrl for=field_secureUrl>SecureUrl</label> <input class=form-control name=secureUrl id=field_secureUrl ng-model=\"cloudinary.secureUrl\"/> </div> <div class=form-group> <label class=control-label translate=reachoutApp.cloudinary.created for=field_created>Created</label> <input id=field_created class=form-control uib-datepicker-popup={{dateformat}} ng-model=cloudinary.created is-open=\"datePickerForCreated.status.opened\"/> <span class=input-group-btn> <button type=button class=\"btn btn-default\" ng-click=datePickerForCreatedOpen($event)><i class=\"glyphicon glyphicon-calendar\"></i></button> </span> </div> <div class=form-group> <label class=control-label translate=reachoutApp.cloudinary.type for=field_type>Type</label> <input class=form-control name=type id=field_type ng-model=\"cloudinary.type\"/> </div> <div class=form-group> <label class=control-label translate=reachoutApp.cloudinary.version for=field_version>Version</label> <input class=form-control name=version id=field_version ng-model=\"cloudinary.version\"/> </div> <div class=form-group> <label class=control-label translate=reachoutApp.cloudinary.url for=field_url>Url</label> <input class=form-control name=url id=field_url ng-model=\"cloudinary.url\"/> </div> <div class=form-group> <label class=control-label translate=reachoutApp.cloudinary.publicId for=field_publicId>PublicId</label> <input class=form-control name=publicId id=field_publicId ng-model=\"cloudinary.publicId\"/> </div> <div class=form-group> <label class=control-label translate=reachoutApp.cloudinary.tags for=field_tags>Tags</label> <input class=form-control name=tags id=field_tags ng-model=\"cloudinary.tags\"/> </div> <div class=form-group> <label class=control-label translate=reachoutApp.cloudinary.orginalFileName for=field_orginalFileName>OrginalFileName</label> <input class=form-control name=orginalFileName id=field_orginalFileName ng-model=\"cloudinary.orginalFileName\"/> </div> <div class=form-group> <label class=control-label translate=reachoutApp.cloudinary.bytes for=field_bytes>Bytes</label> <input type=number class=form-control name=bytes id=field_bytes ng-model=\"cloudinary.bytes\"/> </div> <div class=form-group> <label class=control-label translate=reachoutApp.cloudinary.width for=field_width>Width</label> <input type=number class=form-control name=width id=field_width ng-model=\"cloudinary.width\"/> </div> <div class=form-group> <label class=control-label translate=reachoutApp.cloudinary.eTag for=field_eTag>ETag</label> <input class=form-control name=eTag id=field_eTag ng-model=\"cloudinary.eTag\"/> </div> <div class=form-group> <label class=control-label translate=reachoutApp.cloudinary.height for=field_height>Height</label> <input type=number class=form-control name=height id=field_height ng-model=\"cloudinary.height\"/> </div> </div> <div class=modal-footer> <button type=button class=\"btn btn-default\" data-dismiss=modal ng-click=clear()> <span class=\"glyphicon glyphicon-ban-circle\"></span>&nbsp;<span translate=entity.action.cancel>Cancel</span> </button> <button type=submit ng-disabled=\"editForm.$invalid || isSaving\" class=\"btn btn-primary\"> <span class=\"glyphicon glyphicon-save\"></span>&nbsp;<span translate=entity.action.save>Save</span> </button> </div> </form>"
  );


  $templateCache.put('scripts/app/entities/cloudinary/cloudinarys.html',
    "<div> <h2 translate=reachoutApp.cloudinary.home.title>Cloudinarys</h2> <jh-alert></jh-alert> <div class=container> <div class=row> <div class=col-md-4> <button class=\"btn btn-primary\" ui-sref=cloudinary.new> <span class=\"glyphicon glyphicon-flash\"></span> <span translate=reachoutApp.cloudinary.home.createLabel>Create a new Cloudinary</span> </button> </div> </div> </div> <div class=table-responsive> <table class=\"table table-striped\"> <thead> <tr> <th><span translate=global.field.id>ID</span></th> <th><span translate=reachoutApp.cloudinary.consumerId>ConsumerId</span></th> <th><span translate=reachoutApp.cloudinary.signature>Signature</span></th> <th><span translate=reachoutApp.cloudinary.format>Format</span></th> <th><span translate=reachoutApp.cloudinary.resourceType>ResourceType</span></th> <th><span translate=reachoutApp.cloudinary.secureUrl>SecureUrl</span></th> <th><span translate=reachoutApp.cloudinary.created>Created</span></th> <th><span translate=reachoutApp.cloudinary.type>Type</span></th> <th><span translate=reachoutApp.cloudinary.version>Version</span></th> <th><span translate=reachoutApp.cloudinary.url>Url</span></th> <th><span translate=reachoutApp.cloudinary.publicId>PublicId</span></th> <th><span translate=reachoutApp.cloudinary.tags>Tags</span></th> <th><span translate=reachoutApp.cloudinary.orginalFileName>OrginalFileName</span></th> <th><span translate=reachoutApp.cloudinary.bytes>Bytes</span></th> <th><span translate=reachoutApp.cloudinary.width>Width</span></th> <th><span translate=reachoutApp.cloudinary.eTag>ETag</span></th> <th><span translate=reachoutApp.cloudinary.height>Height</span></th> <th></th> </tr> </thead> <tbody> <tr ng-repeat=\"cloudinary in cloudinarys track by cloudinary.id\"> <td><a ui-sref=cloudinary.detail({id:cloudinary.id})>{{cloudinary.id}}</a></td> <td>{{cloudinary.consumerId}}</td> <td>{{cloudinary.signature}}</td> <td>{{cloudinary.format}}</td> <td>{{cloudinary.resourceType}}</td> <td>{{cloudinary.secureUrl}}</td> <td>{{cloudinary.created | date:'medium'}}</td> <td>{{cloudinary.type}}</td> <td>{{cloudinary.version}}</td> <td>{{cloudinary.url}}</td> <td>{{cloudinary.publicId}}</td> <td>{{cloudinary.tags}}</td> <td>{{cloudinary.orginalFileName}}</td> <td>{{cloudinary.bytes}}</td> <td>{{cloudinary.width}}</td> <td>{{cloudinary.eTag}}</td> <td>{{cloudinary.height}}</td> <td> <button type=submit ui-sref=cloudinary.detail({id:cloudinary.id}) class=\"btn btn-info btn-sm\"> <span class=\"glyphicon glyphicon-eye-open\"></span>&nbsp;<span translate=entity.action.view> View</span> </button> <button type=submit ui-sref=cloudinary.edit({id:cloudinary.id}) class=\"btn btn-primary btn-sm\"> <span class=\"glyphicon glyphicon-pencil\"></span>&nbsp;<span translate=entity.action.edit> Edit</span> </button> <button type=submit ui-sref=cloudinary.delete({id:cloudinary.id}) class=\"btn btn-danger btn-sm\"> <span class=\"glyphicon glyphicon-remove-circle\"></span>&nbsp;<span translate=entity.action.delete> Delete</span> </button> </td> </tr> </tbody> </table> </div> </div>"
  );


  $templateCache.put('scripts/app/entities/consumer/consumer-delete-dialog.html',
    "<form name=deleteForm ng-submit=confirmDelete(consumer.id)> <div class=modal-header> <button type=button class=close data-dismiss=modal aria-hidden=true ng-click=clear()>&times;</button> <h4 class=modal-title translate=entity.delete.title>Confirm delete operation</h4> </div> <div class=modal-body> <p translate=reachoutApp.consumer.delete.question translate-values=\"{id: '{{consumer.id}}'}\">Are you sure you want to delete this Consumer?</p> </div> <div class=modal-footer> <button type=button class=\"btn btn-default\" data-dismiss=modal ng-click=clear()> <span class=\"glyphicon glyphicon-ban-circle\"></span>&nbsp;<span translate=entity.action.cancel>Cancel</span> </button> <button type=submit ng-disabled=deleteForm.$invalid class=\"btn btn-danger\"> <span class=\"glyphicon glyphicon-remove-circle\"></span>&nbsp;<span translate=entity.action.delete>Delete</span> </button> </div> </form>"
  );


  $templateCache.put('scripts/app/entities/consumer/consumer-detail.html',
    "<div> <h2><span translate=reachoutApp.consumer.detail.title>Consumer</span> {{consumer.id}}</h2> <div class=table-responsive> <table class=\"table table-striped\"> <thead> <tr> <th translate=entity.detail.field>Field</th> <th translate=entity.detail.value>Value</th> </tr> </thead> <tbody> <tr> <td> <span translate=reachoutApp.consumer.mobile>Mobile</span> </td> <td> <span class=form-control-static>{{consumer.mobile}}</span> </td> </tr> <tr> <td> <span translate=reachoutApp.consumer.email>Email</span> </td> <td> <span class=form-control-static>{{consumer.email}}</span> </td> </tr> <tr> <td> <span translate=reachoutApp.consumer.status>Status</span> </td> <td> <span class=form-control-static>{{consumer.status}}</span> </td> </tr> <tr> <td> <span translate=reachoutApp.consumer.otp>Otp</span> </td> <td> <span class=form-control-static>{{consumer.otp}}</span> </td> </tr> <tr> <td> <span translate=reachoutApp.consumer.otpCount>OtpCount</span> </td> <td> <span class=form-control-static>{{consumer.otpCount}}</span> </td> </tr> <tr> <td> <span translate=reachoutApp.consumer.created>Created</span> </td> <td> <span class=form-control-static>{{consumer.created | date:'medium'}}</span> </td> </tr> <tr> <td> <span translate=reachoutApp.consumer.updated>Updated</span> </td> <td> <span class=form-control-static>{{consumer.updated | date:'medium'}}</span> </td> </tr> <tr> <td> <span translate=reachoutApp.consumer.name>Name</span> </td> <td> <span class=form-control-static>{{consumer.name}}</span> </td> </tr> </tbody> </table> </div> <button type=submit onclick=window.history.back() class=\"btn btn-info\"> <span class=\"glyphicon glyphicon-arrow-left\"></span>&nbsp;<span translate=entity.action.back> Back</span> </button> </div>"
  );


  $templateCache.put('scripts/app/entities/consumer/consumer-dialog.html',
    "<form name=editForm role=form novalidate ng-submit=save()> <div class=modal-header> <button type=button class=close data-dismiss=modal aria-hidden=true ng-click=clear()>&times;</button> <h4 class=modal-title id=myConsumerLabel translate=reachoutApp.consumer.home.createOrEditLabel>Create or edit a Consumer</h4> </div> <div class=modal-body> <jh-alert-error></jh-alert-error> <div class=form-group> <label for=id translate=global.field.id>ID</label> <input class=form-control id=id name=id ng-model=consumer.id readonly/> </div> <div class=form-group> <label class=control-label translate=reachoutApp.consumer.mobile for=field_mobile>Mobile</label> <input class=form-control name=mobile id=field_mobile ng-model=\"consumer.mobile\"/> </div> <div class=form-group> <label class=control-label translate=reachoutApp.consumer.email for=field_email>Email</label> <input class=form-control name=email id=field_email ng-model=\"consumer.email\"/> </div> <div class=form-group> <label class=control-label translate=reachoutApp.consumer.status for=field_status>Status</label> <input class=form-control name=status id=field_status ng-model=\"consumer.status\"/> </div> <div class=form-group> <label class=control-label translate=reachoutApp.consumer.otp for=field_otp>Otp</label> <input class=form-control name=otp id=field_otp ng-model=\"consumer.otp\"/> </div> <div class=form-group> <label class=control-label translate=reachoutApp.consumer.otpCount for=field_otpCount>OtpCount</label> <input type=number class=form-control name=otpCount id=field_otpCount ng-model=\"consumer.otpCount\"/> </div> <div class=form-group> <label class=control-label translate=reachoutApp.consumer.created for=field_created>Created</label> <input id=field_created class=form-control uib-datepicker-popup={{dateformat}} ng-model=consumer.created is-open=\"datePickerForCreated.status.opened\"/> <span class=input-group-btn> <button type=button class=\"btn btn-default\" ng-click=datePickerForCreatedOpen($event)><i class=\"glyphicon glyphicon-calendar\"></i></button> </span> </div> <div class=form-group> <label class=control-label translate=reachoutApp.consumer.updated for=field_updated>Updated</label> <input id=field_updated class=form-control uib-datepicker-popup={{dateformat}} ng-model=consumer.updated is-open=\"datePickerForUpdated.status.opened\"/> <span class=input-group-btn> <button type=button class=\"btn btn-default\" ng-click=datePickerForUpdatedOpen($event)><i class=\"glyphicon glyphicon-calendar\"></i></button> </span> </div> <div class=form-group> <label class=control-label translate=reachoutApp.consumer.name for=field_name>Name</label> <input class=form-control name=name id=field_name ng-model=\"consumer.name\"/> </div> </div> <div class=modal-footer> <button type=button class=\"btn btn-default\" data-dismiss=modal ng-click=clear()> <span class=\"glyphicon glyphicon-ban-circle\"></span>&nbsp;<span translate=entity.action.cancel>Cancel</span> </button> <button type=submit ng-disabled=\"editForm.$invalid || isSaving\" class=\"btn btn-primary\"> <span class=\"glyphicon glyphicon-save\"></span>&nbsp;<span translate=entity.action.save>Save</span> </button> </div> </form>"
  );


  $templateCache.put('scripts/app/entities/consumer/consumers.html',
    "<div> <h2 translate=reachoutApp.consumer.home.title>Consumers</h2> <jh-alert></jh-alert> <div class=container> <div class=row> <div class=col-md-4> <button class=\"btn btn-primary\" ui-sref=consumer.new> <span class=\"glyphicon glyphicon-flash\"></span> <span translate=reachoutApp.consumer.home.createLabel>Create a new Consumer</span> </button> </div> </div> </div> <div class=table-responsive> <table class=\"table table-striped\"> <thead> <tr> <th><span translate=global.field.id>ID</span></th> <th><span translate=reachoutApp.consumer.mobile>Mobile</span></th> <th><span translate=reachoutApp.consumer.email>Email</span></th> <th><span translate=reachoutApp.consumer.status>Status</span></th> <th><span translate=reachoutApp.consumer.otp>Otp</span></th> <th><span translate=reachoutApp.consumer.otpCount>OtpCount</span></th> <th><span translate=reachoutApp.consumer.created>Created</span></th> <th><span translate=reachoutApp.consumer.updated>Updated</span></th> <th><span translate=reachoutApp.consumer.name>Name</span></th> <th><select ng-model=search> <option value=\"\">Active</option> <option value=true>true</option> <option value=false>false</option> </select></th> <th></th> </tr> </thead> <tbody> <tr ng-repeat=\"consumer in consumers | filter:search track by consumer.id\"> <td><a ui-sref=consumer.detail({id:consumer.id})>{{consumer.id}}</a></td> <td>{{consumer.mobile}}</td> <td>{{consumer.email}}</td> <td>{{consumer.status}}</td> <td>{{consumer.otp}}</td> <td>{{consumer.otpCount}}</td> <td>{{consumer.created | date:'medium'}}</td> <td>{{consumer.updated | date:'medium'}}</td> <td>{{consumer.name}}</td> <td> <button type=submit ng-click=active(consumer.id,consumer.active) class=\"btn btn-info btn-sm\"> <span class=\"glyphicon glyphicon-eye-open\"></span>&nbsp;<span> {{consumer.active == true ? 'Activated' : 'Deactivated'}}</span> </button> </td> <td> <button type=submit ui-sref=consumer.detail({id:consumer.id}) class=\"btn btn-info btn-sm\"> <span class=\"glyphicon glyphicon-eye-open\"></span>&nbsp;<span translate=entity.action.view> View</span> </button> <button type=submit ui-sref=consumer.edit({id:consumer.id}) class=\"btn btn-primary btn-sm\"> <span class=\"glyphicon glyphicon-pencil\"></span>&nbsp;<span translate=entity.action.edit> Edit</span> </button> <button type=submit ui-sref=consumer.delete({id:consumer.id}) class=\"btn btn-danger btn-sm\"> <span class=\"glyphicon glyphicon-remove-circle\"></span>&nbsp;<span translate=entity.action.delete> Delete</span> </button> </td> </tr> </tbody> </table> </div> </div>"
  );


  $templateCache.put('scripts/app/entities/consumerFavourite/consumerFavourite-delete-dialog.html',
    "<form name=deleteForm ng-submit=confirmDelete(consumerFavourite.id)> <div class=modal-header> <button type=button class=close data-dismiss=modal aria-hidden=true ng-click=clear()>&times;</button> <h4 class=modal-title translate=entity.delete.title>Confirm delete operation</h4> </div> <div class=modal-body> <p translate=reachoutApp.consumerFavourite.delete.question translate-values=\"{id: '{{consumerFavourite.id}}'}\">Are you sure you want to delete this ConsumerFavourite?</p> </div> <div class=modal-footer> <button type=button class=\"btn btn-default\" data-dismiss=modal ng-click=clear()> <span class=\"glyphicon glyphicon-ban-circle\"></span>&nbsp;<span translate=entity.action.cancel>Cancel</span> </button> <button type=submit ng-disabled=deleteForm.$invalid class=\"btn btn-danger\"> <span class=\"glyphicon glyphicon-remove-circle\"></span>&nbsp;<span translate=entity.action.delete>Delete</span> </button> </div> </form>"
  );


  $templateCache.put('scripts/app/entities/consumerFavourite/consumerFavourite-detail.html',
    "<div> <h2><span translate=reachoutApp.consumerFavourite.detail.title>ConsumerFavourite</span> {{consumerFavourite.id}}</h2> <div class=table-responsive> <table class=\"table table-striped\"> <thead> <tr> <th translate=entity.detail.field>Field</th> <th translate=entity.detail.value>Value</th> </tr> </thead> <tbody> <tr> <td> <span translate=reachoutApp.consumerFavourite.consumerId>ConsumerId</span> </td> <td> <span class=form-control-static>{{consumerFavourite.consumerId}}</span> </td> </tr> <tr> <td> <span translate=reachoutApp.consumerFavourite.providerId>ProviderId</span> </td> <td> <span class=form-control-static>{{consumerFavourite.providerId}}</span> </td> </tr> <tr> <td> <span translate=reachoutApp.consumerFavourite.created>Created</span> </td> <td> <span class=form-control-static>{{consumerFavourite.created | date:'medium'}}</span> </td> </tr> <tr> <td> <span translate=reachoutApp.consumerFavourite.updated>Updated</span> </td> <td> <span class=form-control-static>{{consumerFavourite.updated | date:'medium'}}</span> </td> </tr> </tbody> </table> </div> <button type=submit onclick=window.history.back() class=\"btn btn-info\"> <span class=\"glyphicon glyphicon-arrow-left\"></span>&nbsp;<span translate=entity.action.back> Back</span> </button> </div>"
  );


  $templateCache.put('scripts/app/entities/consumerFavourite/consumerFavourite-dialog.html',
    "<form name=editForm role=form novalidate ng-submit=save()> <div class=modal-header> <button type=button class=close data-dismiss=modal aria-hidden=true ng-click=clear()>&times;</button> <h4 class=modal-title id=myConsumerFavouriteLabel translate=reachoutApp.consumerFavourite.home.createOrEditLabel>Create or edit a ConsumerFavourite</h4> </div> <div class=modal-body> <jh-alert-error></jh-alert-error> <div class=form-group> <label for=id translate=global.field.id>ID</label> <input class=form-control id=id name=id ng-model=consumerFavourite.id readonly/> </div> <div class=form-group> <label class=control-label translate=reachoutApp.consumerFavourite.consumerId for=field_consumerId>ConsumerId</label> <input class=form-control name=consumerId id=field_consumerId ng-model=\"consumerFavourite.consumerId\"/> </div> <div class=form-group> <label class=control-label translate=reachoutApp.consumerFavourite.providerId for=field_providerId>ProviderId</label> <input class=form-control name=providerId id=field_providerId ng-model=\"consumerFavourite.providerId\"/> </div> <div class=form-group> <label class=control-label translate=reachoutApp.consumerFavourite.created for=field_created>Created</label> <input id=field_created class=form-control uib-datepicker-popup={{dateformat}} ng-model=consumerFavourite.created is-open=\"datePickerForCreated.status.opened\"/> <span class=input-group-btn> <button type=button class=\"btn btn-default\" ng-click=datePickerForCreatedOpen($event)><i class=\"glyphicon glyphicon-calendar\"></i></button> </span> </div> <div class=form-group> <label class=control-label translate=reachoutApp.consumerFavourite.updated for=field_updated>Updated</label> <input id=field_updated class=form-control uib-datepicker-popup={{dateformat}} ng-model=consumerFavourite.updated is-open=\"datePickerForUpdated.status.opened\"/> <span class=input-group-btn> <button type=button class=\"btn btn-default\" ng-click=datePickerForUpdatedOpen($event)><i class=\"glyphicon glyphicon-calendar\"></i></button> </span> </div> </div> <div class=modal-footer> <button type=button class=\"btn btn-default\" data-dismiss=modal ng-click=clear()> <span class=\"glyphicon glyphicon-ban-circle\"></span>&nbsp;<span translate=entity.action.cancel>Cancel</span> </button> <button type=submit ng-disabled=\"editForm.$invalid || isSaving\" class=\"btn btn-primary\"> <span class=\"glyphicon glyphicon-save\"></span>&nbsp;<span translate=entity.action.save>Save</span> </button> </div> </form>"
  );


  $templateCache.put('scripts/app/entities/consumerFavourite/consumerFavourites.html',
    "<div> <h2 translate=reachoutApp.consumerFavourite.home.title>ConsumerFavourites</h2> <jh-alert></jh-alert> <div class=container> <div class=row> <div class=col-md-4> <button class=\"btn btn-primary\" ui-sref=consumerFavourite.new> <span class=\"glyphicon glyphicon-flash\"></span> <span translate=reachoutApp.consumerFavourite.home.createLabel>Create a new ConsumerFavourite</span> </button> </div> </div> </div> <div class=table-responsive> <table class=\"table table-striped\"> <thead> <tr> <th><span translate=global.field.id>ID</span></th> <th><span translate=reachoutApp.consumerFavourite.consumerId>ConsumerId</span></th> <th><span translate=reachoutApp.consumerFavourite.providerId>ProviderId</span></th> <th><span translate=reachoutApp.consumerFavourite.created>Created</span></th> <th><span translate=reachoutApp.consumerFavourite.updated>Updated</span></th> <th></th> </tr> </thead> <tbody> <tr ng-repeat=\"consumerFavourite in consumerFavourites track by consumerFavourite.id\"> <td><a ui-sref=consumerFavourite.detail({id:consumerFavourite.id})>{{consumerFavourite.id}}</a></td> <td>{{consumerFavourite.consumerId}}</td> <td>{{consumerFavourite.providerId}}</td> <td>{{consumerFavourite.created | date:'medium'}}</td> <td>{{consumerFavourite.updated | date:'medium'}}</td> <td> <button type=submit ui-sref=consumerFavourite.detail({id:consumerFavourite.id}) class=\"btn btn-info btn-sm\"> <span class=\"glyphicon glyphicon-eye-open\"></span>&nbsp;<span translate=entity.action.view> View</span> </button> <button type=submit ui-sref=consumerFavourite.edit({id:consumerFavourite.id}) class=\"btn btn-primary btn-sm\"> <span class=\"glyphicon glyphicon-pencil\"></span>&nbsp;<span translate=entity.action.edit> Edit</span> </button> <button type=submit ui-sref=consumerFavourite.delete({id:consumerFavourite.id}) class=\"btn btn-danger btn-sm\"> <span class=\"glyphicon glyphicon-remove-circle\"></span>&nbsp;<span translate=entity.action.delete> Delete</span> </button> </td> </tr> </tbody> </table> </div> </div>"
  );


  $templateCache.put('scripts/app/entities/consumerFeedback/consumerFeedback-delete-dialog.html',
    "<form name=deleteForm ng-submit=confirmDelete(consumerFeedback.id)> <div class=modal-header> <button type=button class=close data-dismiss=modal aria-hidden=true ng-click=clear()>&times;</button> <h4 class=modal-title translate=entity.delete.title>Confirm delete operation</h4> </div> <div class=modal-body> <p translate=reachoutApp.consumerFeedback.delete.question translate-values=\"{id: '{{consumerFeedback.id}}'}\">Are you sure you want to delete this ConsumerFeedback?</p> </div> <div class=modal-footer> <button type=button class=\"btn btn-default\" data-dismiss=modal ng-click=clear()> <span class=\"glyphicon glyphicon-ban-circle\"></span>&nbsp;<span translate=entity.action.cancel>Cancel</span> </button> <button type=submit ng-disabled=deleteForm.$invalid class=\"btn btn-danger\"> <span class=\"glyphicon glyphicon-remove-circle\"></span>&nbsp;<span translate=entity.action.delete>Delete</span> </button> </div> </form>"
  );


  $templateCache.put('scripts/app/entities/consumerFeedback/consumerFeedback-detail.html',
    "<div> <h2><span translate=reachoutApp.consumerFeedback.detail.title>ConsumerFeedback</span> {{consumerFeedback.id}}</h2> <div class=table-responsive> <table class=\"table table-striped\"> <thead> <tr> <th translate=entity.detail.field>Field</th> <th translate=entity.detail.value>Value</th> </tr> </thead> <tbody> <tr> <td> <span translate=reachoutApp.consumerFeedback.comment>Comment</span> </td> <td> <span class=form-control-static>{{consumerFeedback.comment}}</span> </td> </tr> <tr> <td> <span translate=reachoutApp.consumerFeedback.likeOrDislike>LikeOrDislike</span> </td> <td> <span class=form-control-static>{{consumerFeedback.likeOrDislike}}</span> </td> </tr> <tr> <td> <span translate=reachoutApp.consumerFeedback.notificationId>NotificationId</span> </td> <td> <span class=form-control-static>{{consumerFeedback.notificationId}}</span> </td> </tr> <tr> <td> <span translate=reachoutApp.consumerFeedback.consumerId>ConsumerId</span> </td> <td> <span class=form-control-static>{{consumerFeedback.consumerId}}</span> </td> </tr> </tbody> </table> </div> <button type=submit onclick=window.history.back() class=\"btn btn-info\"> <span class=\"glyphicon glyphicon-arrow-left\"></span>&nbsp;<span translate=entity.action.back> Back</span> </button> </div>"
  );


  $templateCache.put('scripts/app/entities/consumerFeedback/consumerFeedback-dialog.html',
    "<form name=editForm role=form novalidate ng-submit=save()> <div class=modal-header> <button type=button class=close data-dismiss=modal aria-hidden=true ng-click=clear()>&times;</button> <h4 class=modal-title id=myConsumerFeedbackLabel translate=reachoutApp.consumerFeedback.home.createOrEditLabel>Create or edit a ConsumerFeedback</h4> </div> <div class=modal-body> <jh-alert-error></jh-alert-error> <div class=form-group> <label for=id translate=global.field.id>ID</label> <input class=form-control id=id name=id ng-model=consumerFeedback.id readonly/> </div> <div class=form-group> <label class=control-label translate=reachoutApp.consumerFeedback.comment for=field_comment>Comment</label> <input class=form-control name=comment id=field_comment ng-model=\"consumerFeedback.comment\"/> </div> <div class=form-group> <label class=control-label translate=reachoutApp.consumerFeedback.likeOrDislike for=field_likeOrDislike>LikeOrDislike</label> <input class=form-control name=likeOrDislike id=field_likeOrDislike ng-model=\"consumerFeedback.likeOrDislike\"/> </div> <div class=form-group> <label class=control-label translate=reachoutApp.consumerFeedback.notificationId for=field_notificationId>NotificationId</label> <input class=form-control name=notificationId id=field_notificationId ng-model=\"consumerFeedback.notificationId\"/> </div> <div class=form-group> <label class=control-label translate=reachoutApp.consumerFeedback.consumerId for=field_consumerId>ConsumerId</label> <input class=form-control name=consumerId id=field_consumerId ng-model=\"consumerFeedback.consumerId\"/> </div> </div> <div class=modal-footer> <button type=button class=\"btn btn-default\" data-dismiss=modal ng-click=clear()> <span class=\"glyphicon glyphicon-ban-circle\"></span>&nbsp;<span translate=entity.action.cancel>Cancel</span> </button> <button type=submit ng-disabled=\"editForm.$invalid || isSaving\" class=\"btn btn-primary\"> <span class=\"glyphicon glyphicon-save\"></span>&nbsp;<span translate=entity.action.save>Save</span> </button> </div> </form>"
  );


  $templateCache.put('scripts/app/entities/consumerFeedback/consumerFeedbacks.html',
    "<div> <h2 translate=reachoutApp.consumerFeedback.home.title>ConsumerFeedbacks</h2> <jh-alert></jh-alert> <div class=container> <div class=row> <div class=col-md-4> <button class=\"btn btn-primary\" ui-sref=consumerFeedback.new> <span class=\"glyphicon glyphicon-flash\"></span> <span translate=reachoutApp.consumerFeedback.home.createLabel>Create a new ConsumerFeedback</span> </button> </div> </div> </div> <div class=table-responsive> <table class=\"table table-striped\"> <thead> <tr> <th><span translate=global.field.id>ID</span></th> <th><span translate=reachoutApp.consumerFeedback.comment>Comment</span></th> <th><span translate=reachoutApp.consumerFeedback.likeOrDislike>LikeOrDislike</span></th> <th><span translate=reachoutApp.consumerFeedback.notificationId>NotificationId</span></th> <th><span translate=reachoutApp.consumerFeedback.consumerId>ConsumerId</span></th> <th></th> </tr> </thead> <tbody> <tr ng-repeat=\"consumerFeedback in consumerFeedbacks track by consumerFeedback.id\"> <td><a ui-sref=consumerFeedback.detail({id:consumerFeedback.id})>{{consumerFeedback.id}}</a></td> <td>{{consumerFeedback.comment}}</td> <td>{{consumerFeedback.likeOrDislike}}</td> <td>{{consumerFeedback.notificationId}}</td> <td>{{consumerFeedback.consumerId}}</td> <td> <button type=submit ui-sref=consumerFeedback.detail({id:consumerFeedback.id}) class=\"btn btn-info btn-sm\"> <span class=\"glyphicon glyphicon-eye-open\"></span>&nbsp;<span translate=entity.action.view> View</span> </button> <button type=submit ui-sref=consumerFeedback.edit({id:consumerFeedback.id}) class=\"btn btn-primary btn-sm\"> <span class=\"glyphicon glyphicon-pencil\"></span>&nbsp;<span translate=entity.action.edit> Edit</span> </button> <button type=submit ui-sref=consumerFeedback.delete({id:consumerFeedback.id}) class=\"btn btn-danger btn-sm\"> <span class=\"glyphicon glyphicon-remove-circle\"></span>&nbsp;<span translate=entity.action.delete> Delete</span> </button> </td> </tr> </tbody> </table> </div> </div>"
  );


  $templateCache.put('scripts/app/entities/consumerRegions/consumerRegions-delete-dialog.html',
    "<form name=deleteForm ng-submit=confirmDelete(consumerRegions.id)> <div class=modal-header> <button type=button class=close data-dismiss=modal aria-hidden=true ng-click=clear()>&times;</button> <h4 class=modal-title translate=entity.delete.title>Confirm delete operation</h4> </div> <div class=modal-body> <p translate=reachoutApp.consumerRegions.delete.question translate-values=\"{id: '{{consumerRegions.id}}'}\">Are you sure you want to delete this ConsumerRegions?</p> </div> <div class=modal-footer> <button type=button class=\"btn btn-default\" data-dismiss=modal ng-click=clear()> <span class=\"glyphicon glyphicon-ban-circle\"></span>&nbsp;<span translate=entity.action.cancel>Cancel</span> </button> <button type=submit ng-disabled=deleteForm.$invalid class=\"btn btn-danger\"> <span class=\"glyphicon glyphicon-remove-circle\"></span>&nbsp;<span translate=entity.action.delete>Delete</span> </button> </div> </form>"
  );


  $templateCache.put('scripts/app/entities/consumerRegions/consumerRegions-detail.html',
    "<div> <h2><span translate=reachoutApp.consumerRegions.detail.title>ConsumerRegions</span> {{consumerRegions.id}}</h2> <div class=table-responsive> <table class=\"table table-striped\"> <thead> <tr> <th translate=entity.detail.field>Field</th> <th translate=entity.detail.value>Value</th> </tr> </thead> <tbody> <tr> <td> <span translate=reachoutApp.consumerRegions.consumerId>ConsumerId</span> </td> <td> <span class=form-control-static>{{consumerRegions.consumerId}}</span> </td> </tr> <tr> <td> <span translate=reachoutApp.consumerRegions.region>Region</span> </td> <td> <span class=form-control-static>{{consumerRegions.region}}</span> </td> </tr> </tbody> </table> </div> <button type=submit onclick=window.history.back() class=\"btn btn-info\"> <span class=\"glyphicon glyphicon-arrow-left\"></span>&nbsp;<span translate=entity.action.back> Back</span> </button> </div>"
  );


  $templateCache.put('scripts/app/entities/consumerRegions/consumerRegions-dialog.html',
    "<form name=editForm role=form novalidate ng-submit=save()> <div class=modal-header> <button type=button class=close data-dismiss=modal aria-hidden=true ng-click=clear()>&times;</button> <h4 class=modal-title id=myConsumerRegionsLabel translate=reachoutApp.consumerRegions.home.createOrEditLabel>Create or edit a ConsumerRegions</h4> </div> <div class=modal-body> <jh-alert-error></jh-alert-error> <div class=form-group> <label for=id translate=global.field.id>ID</label> <input class=form-control id=id name=id ng-model=consumerRegions.id readonly/> </div> <div class=form-group> <label class=control-label translate=reachoutApp.consumerRegions.consumerId for=field_consumerId>ConsumerId</label> <input class=form-control name=consumerId id=field_consumerId ng-model=\"consumerRegions.consumerId\"/> </div> <div class=form-group> <label class=control-label translate=reachoutApp.consumerRegions.region for=field_region>Region</label> <input class=form-control name=region id=field_region ng-model=\"consumerRegions.region\"/> </div> </div> <div class=modal-footer> <button type=button class=\"btn btn-default\" data-dismiss=modal ng-click=clear()> <span class=\"glyphicon glyphicon-ban-circle\"></span>&nbsp;<span translate=entity.action.cancel>Cancel</span> </button> <button type=submit ng-disabled=\"editForm.$invalid || isSaving\" class=\"btn btn-primary\"> <span class=\"glyphicon glyphicon-save\"></span>&nbsp;<span translate=entity.action.save>Save</span> </button> </div> </form>"
  );


  $templateCache.put('scripts/app/entities/consumerRegions/consumerRegionss.html',
    "<div> <h2 translate=reachoutApp.consumerRegions.home.title>ConsumerRegionss</h2> <jh-alert></jh-alert> <div class=container> <div class=row> <div class=col-md-4> <button class=\"btn btn-primary\" ui-sref=consumerRegions.new> <span class=\"glyphicon glyphicon-flash\"></span> <span translate=reachoutApp.consumerRegions.home.createLabel>Create a new ConsumerRegions</span> </button> </div> </div> </div> <div class=table-responsive> <table class=\"table table-striped\"> <thead> <tr> <th><span translate=global.field.id>ID</span></th> <th><span translate=reachoutApp.consumerRegions.consumerId>ConsumerId</span></th> <th><span translate=reachoutApp.consumerRegions.region>Region</span></th> <th></th> </tr> </thead> <tbody> <tr ng-repeat=\"consumerRegions in consumerRegionss track by consumerRegions.id\"> <td><a ui-sref=consumerRegions.detail({id:consumerRegions.id})>{{consumerRegions.id}}</a></td> <td>{{consumerRegions.consumerId}}</td> <td>{{consumerRegions.region}}</td> <td> <button type=submit ui-sref=consumerRegions.detail({id:consumerRegions.id}) class=\"btn btn-info btn-sm\"> <span class=\"glyphicon glyphicon-eye-open\"></span>&nbsp;<span translate=entity.action.view> View</span> </button> <button type=submit ui-sref=consumerRegions.edit({id:consumerRegions.id}) class=\"btn btn-primary btn-sm\"> <span class=\"glyphicon glyphicon-pencil\"></span>&nbsp;<span translate=entity.action.edit> Edit</span> </button> <button type=submit ui-sref=consumerRegions.delete({id:consumerRegions.id}) class=\"btn btn-danger btn-sm\"> <span class=\"glyphicon glyphicon-remove-circle\"></span>&nbsp;<span translate=entity.action.delete> Delete</span> </button> </td> </tr> </tbody> </table> </div> </div>"
  );


  $templateCache.put('scripts/app/entities/deviceInfo/deviceInfo-delete-dialog.html',
    "<form name=deleteForm ng-submit=confirmDelete(deviceInfo.id)> <div class=modal-header> <button type=button class=close data-dismiss=modal aria-hidden=true ng-click=clear()>&times;</button> <h4 class=modal-title translate=entity.delete.title>Confirm delete operation</h4> </div> <div class=modal-body> <p translate=reachoutApp.deviceInfo.delete.question translate-values=\"{id: '{{deviceInfo.id}}'}\">Are you sure you want to delete this DeviceInfo?</p> </div> <div class=modal-footer> <button type=button class=\"btn btn-default\" data-dismiss=modal ng-click=clear()> <span class=\"glyphicon glyphicon-ban-circle\"></span>&nbsp;<span translate=entity.action.cancel>Cancel</span> </button> <button type=submit ng-disabled=deleteForm.$invalid class=\"btn btn-danger\"> <span class=\"glyphicon glyphicon-remove-circle\"></span>&nbsp;<span translate=entity.action.delete>Delete</span> </button> </div> </form>"
  );


  $templateCache.put('scripts/app/entities/deviceInfo/deviceInfo-detail.html',
    "<div> <h2><span translate=reachoutApp.deviceInfo.detail.title>DeviceInfo</span> {{deviceInfo.id}}</h2> <div class=table-responsive> <table class=\"table table-striped\"> <thead> <tr> <th translate=entity.detail.field>Field</th> <th translate=entity.detail.value>Value</th> </tr> </thead> <tbody> <tr> <td> <span translate=reachoutApp.deviceInfo.device>Device</span> </td> <td> <span class=form-control-static>{{deviceInfo.device}}</span> </td> </tr> <tr> <td> <span translate=reachoutApp.deviceInfo.sdk>Sdk</span> </td> <td> <span class=form-control-static>{{deviceInfo.sdk}}</span> </td> </tr> <tr> <td> <span translate=reachoutApp.deviceInfo.model>Model</span> </td> <td> <span class=form-control-static>{{deviceInfo.model}}</span> </td> </tr> <tr> <td> <span translate=reachoutApp.deviceInfo.product>Product</span> </td> <td> <span class=form-control-static>{{deviceInfo.product}}</span> </td> </tr> <tr> <td> <span translate=reachoutApp.deviceInfo.consumerId>ConsumerId</span> </td> <td> <span class=form-control-static>{{deviceInfo.consumerId}}</span> </td> </tr> </tbody> </table> </div> <button type=submit onclick=window.history.back() class=\"btn btn-info\"> <span class=\"glyphicon glyphicon-arrow-left\"></span>&nbsp;<span translate=entity.action.back> Back</span> </button> </div>"
  );


  $templateCache.put('scripts/app/entities/deviceInfo/deviceInfo-dialog.html',
    "<form name=editForm role=form novalidate ng-submit=save()> <div class=modal-header> <button type=button class=close data-dismiss=modal aria-hidden=true ng-click=clear()>&times;</button> <h4 class=modal-title id=myDeviceInfoLabel translate=reachoutApp.deviceInfo.home.createOrEditLabel>Create or edit a DeviceInfo</h4> </div> <div class=modal-body> <jh-alert-error></jh-alert-error> <div class=form-group> <label for=id translate=global.field.id>ID</label> <input class=form-control id=id name=id ng-model=deviceInfo.id readonly/> </div> <div class=form-group> <label class=control-label translate=reachoutApp.deviceInfo.device for=field_device>Device</label> <input class=form-control name=device id=field_device ng-model=\"deviceInfo.device\"/> </div> <div class=form-group> <label class=control-label translate=reachoutApp.deviceInfo.sdk for=field_sdk>Sdk</label> <input class=form-control name=sdk id=field_sdk ng-model=\"deviceInfo.sdk\"/> </div> <div class=form-group> <label class=control-label translate=reachoutApp.deviceInfo.model for=field_model>Model</label> <input class=form-control name=model id=field_model ng-model=\"deviceInfo.model\"/> </div> <div class=form-group> <label class=control-label translate=reachoutApp.deviceInfo.product for=field_product>Product</label> <input class=form-control name=product id=field_product ng-model=\"deviceInfo.product\"/> </div> <div class=form-group> <label class=control-label translate=reachoutApp.deviceInfo.consumerId for=field_consumerId>ConsumerId</label> <input class=form-control name=consumerId id=field_consumerId ng-model=\"deviceInfo.consumerId\"/> </div> </div> <div class=modal-footer> <button type=button class=\"btn btn-default\" data-dismiss=modal ng-click=clear()> <span class=\"glyphicon glyphicon-ban-circle\"></span>&nbsp;<span translate=entity.action.cancel>Cancel</span> </button> <button type=submit ng-disabled=\"editForm.$invalid || isSaving\" class=\"btn btn-primary\"> <span class=\"glyphicon glyphicon-save\"></span>&nbsp;<span translate=entity.action.save>Save</span> </button> </div> </form>"
  );


  $templateCache.put('scripts/app/entities/deviceInfo/deviceInfos.html',
    "<div> <h2 translate=reachoutApp.deviceInfo.home.title>DeviceInfos</h2> <jh-alert></jh-alert> <div class=container> <div class=row> <div class=col-md-4> <button class=\"btn btn-primary\" ui-sref=deviceInfo.new> <span class=\"glyphicon glyphicon-flash\"></span> <span translate=reachoutApp.deviceInfo.home.createLabel>Create a new DeviceInfo</span> </button> </div> </div> </div> <div class=table-responsive> <table class=\"table table-striped\"> <thead> <tr> <th><span translate=global.field.id>ID</span></th> <th><span translate=reachoutApp.deviceInfo.device>Device</span></th> <th><span translate=reachoutApp.deviceInfo.sdk>Sdk</span></th> <th><span translate=reachoutApp.deviceInfo.model>Model</span></th> <th><span translate=reachoutApp.deviceInfo.product>Product</span></th> <th><span translate=reachoutApp.deviceInfo.consumerId>ConsumerId</span></th> <th></th> </tr> </thead> <tbody> <tr ng-repeat=\"deviceInfo in deviceInfos track by deviceInfo.id\"> <td><a ui-sref=deviceInfo.detail({id:deviceInfo.id})>{{deviceInfo.id}}</a></td> <td>{{deviceInfo.device}}</td> <td>{{deviceInfo.sdk}}</td> <td>{{deviceInfo.model}}</td> <td>{{deviceInfo.product}}</td> <td>{{deviceInfo.consumerId}}</td> <td> <button type=submit ui-sref=deviceInfo.detail({id:deviceInfo.id}) class=\"btn btn-info btn-sm\"> <span class=\"glyphicon glyphicon-eye-open\"></span>&nbsp;<span translate=entity.action.view> View</span> </button> <button type=submit ui-sref=deviceInfo.edit({id:deviceInfo.id}) class=\"btn btn-primary btn-sm\"> <span class=\"glyphicon glyphicon-pencil\"></span>&nbsp;<span translate=entity.action.edit> Edit</span> </button> <button type=submit ui-sref=deviceInfo.delete({id:deviceInfo.id}) class=\"btn btn-danger btn-sm\"> <span class=\"glyphicon glyphicon-remove-circle\"></span>&nbsp;<span translate=entity.action.delete> Delete</span> </button> </td> </tr> </tbody> </table> </div> </div>"
  );


  $templateCache.put('scripts/app/entities/notification/notification-delete-dialog.html',
    "<form name=deleteForm ng-submit=confirmDelete(notification.id)> <div class=modal-header> <button type=button class=close data-dismiss=modal aria-hidden=true ng-click=clear()>&times;</button> <h4 class=modal-title translate=entity.delete.title>Confirm delete operation</h4> </div> <div class=modal-body> <p translate=reachoutApp.notification.delete.question translate-values=\"{id: '{{notification.id}}'}\">Are you sure you want to delete this Notification?</p> </div> <div class=modal-footer> <button type=button class=\"btn btn-default\" data-dismiss=modal ng-click=clear()> <span class=\"glyphicon glyphicon-ban-circle\"></span>&nbsp;<span translate=entity.action.cancel>Cancel</span> </button> <button type=submit ng-disabled=deleteForm.$invalid class=\"btn btn-danger\"> <span class=\"glyphicon glyphicon-remove-circle\"></span>&nbsp;<span translate=entity.action.delete>Delete</span> </button> </div> </form>"
  );


  $templateCache.put('scripts/app/entities/notification/notification-detail.html',
    "<div> <h2><span translate=reachoutApp.notification.detail.title>Notification</span> {{notification.id}}</h2> <div class=table-responsive> <table class=\"table table-striped\"> <thead> <tr> <th translate=entity.detail.field>Field</th> <th translate=entity.detail.value>Value</th> </tr> </thead> <tbody> <tr> <td> <span translate=reachoutApp.notification.categery>Categery</span> </td> <td> <span class=form-control-static>{{notification.categery}}</span> </td> </tr> <tr> <td> <span translate=reachoutApp.notification.title>Title</span> </td> <td> <span class=form-control-static>{{notification.title}}</span> </td> </tr> <tr> <td> <span translate=reachoutApp.notification.description>Description</span> </td> <td> <span class=form-control-static>{{notification.description}}</span> </td> </tr> <tr> <td> <span translate=reachoutApp.notification.validFrom>ValidFrom</span> </td> <td> <span class=form-control-static>{{notification.validFrom | date:'medium'}}</span> </td> </tr> <tr> <td> <span translate=reachoutApp.notification.validTo>ValidTo</span> </td> <td> <span class=form-control-static>{{notification.validTo | date:'medium'}}</span> </td> </tr> <tr> <td> <span translate=reachoutApp.notification.secureUrl>SecureUrl</span> </td> <td> <span class=form-control-static>{{notification.secureUrl}}</span> </td> </tr> <tr> <td> <span translate=reachoutApp.notification.expieryDate>ExpieryDate</span> </td> <td> <span class=form-control-static>{{notification.expieryDate | date:'medium'}}</span> </td> </tr> <tr> <td> <span translate=reachoutApp.notification.delivered>Delivered</span> </td> <td> <span class=form-control-static>{{notification.delivered}}</span> </td> </tr> <tr> <td> <span translate=reachoutApp.notification.active>Active</span> </td> <td> <span class=form-control-static>{{notification.active}}</span> </td> </tr> <tr> <td> <span translate=reachoutApp.notification.created>Created</span> </td> <td> <span class=form-control-static>{{notification.created | date:'medium'}}</span> </td> </tr> <tr> <td> <span translate=reachoutApp.notification.updated>Updated</span> </td> <td> <span class=form-control-static>{{notification.updated | date:'medium'}}</span> </td> </tr> <tr> <td> <span translate=reachoutApp.notification.offensive>Offensive</span> </td> <td> <span class=form-control-static>{{notification.offensive}}</span> </td> </tr> <tr> <td> <span translate=reachoutApp.notification.publicId>PublicId</span> </td> <td> <span class=form-control-static>{{notification.publicId}}</span> </td> </tr> <tr> <td> <span translate=reachoutApp.notification.url>Url</span> </td> <td> <span class=form-control-static>{{notification.url}}</span> </td> </tr> <tr> <td> <span translate=reachoutApp.notification.consumerId>ConsumerId</span> </td> <td> <span class=form-control-static>{{notification.consumerId}}</span> </td> </tr> </tbody> </table> </div> <button type=submit onclick=window.history.back() class=\"btn btn-info\"> <span class=\"glyphicon glyphicon-arrow-left\"></span>&nbsp;<span translate=entity.action.back> Back</span> </button> </div>"
  );


  $templateCache.put('scripts/app/entities/notification/notification-dialog.html',
    "<form name=editForm role=form novalidate ng-submit=save()> <div class=modal-header> <button type=button class=close data-dismiss=modal aria-hidden=true ng-click=clear()>&times;</button> <h4 class=modal-title id=myNotificationLabel translate=reachoutApp.notification.home.createOrEditLabel>Create or edit a Notification</h4> </div> <div class=modal-body> <jh-alert-error></jh-alert-error> <div class=form-group> <label for=id translate=global.field.id>ID</label> <input class=form-control id=id name=id ng-model=notification.id readonly/> </div> <div class=form-group> <label class=control-label translate=reachoutApp.notification.categery for=field_categery>Categery</label> <input class=form-control name=categery id=field_categery ng-model=\"notification.categery\"/> </div> <div class=form-group> <label class=control-label translate=reachoutApp.notification.title for=field_title>Title</label> <input class=form-control name=title id=field_title ng-model=\"notification.title\"/> </div> <div class=form-group> <label class=control-label translate=reachoutApp.notification.description for=field_description>Description</label> <input class=form-control name=description id=field_description ng-model=\"notification.description\"/> </div> <div class=form-group> <label class=control-label translate=reachoutApp.notification.validFrom for=field_validFrom>ValidFrom</label> <input id=field_validFrom class=form-control uib-datepicker-popup={{dateformat}} ng-model=notification.validFrom is-open=\"datePickerForValidFrom.status.opened\"/> <span class=input-group-btn> <button type=button class=\"btn btn-default\" ng-click=datePickerForValidFromOpen($event)><i class=\"glyphicon glyphicon-calendar\"></i></button> </span> </div> <div class=form-group> <label class=control-label translate=reachoutApp.notification.validTo for=field_validTo>ValidTo</label> <input id=field_validTo class=form-control uib-datepicker-popup={{dateformat}} ng-model=notification.validTo is-open=\"datePickerForValidTo.status.opened\"/> <span class=input-group-btn> <button type=button class=\"btn btn-default\" ng-click=datePickerForValidToOpen($event)><i class=\"glyphicon glyphicon-calendar\"></i></button> </span> </div> <div class=form-group> <label class=control-label translate=reachoutApp.notification.secureUrl for=field_secureUrl>SecureUrl</label> <input class=form-control name=secureUrl id=field_secureUrl ng-model=\"notification.secureUrl\"/> </div> <div class=form-group> <label class=control-label translate=reachoutApp.notification.expieryDate for=field_expieryDate>ExpieryDate</label> <input id=field_expieryDate class=form-control uib-datepicker-popup={{dateformat}} ng-model=notification.expieryDate is-open=\"datePickerForExpieryDate.status.opened\"/> <span class=input-group-btn> <button type=button class=\"btn btn-default\" ng-click=datePickerForExpieryDateOpen($event)><i class=\"glyphicon glyphicon-calendar\"></i></button> </span> </div> <div class=form-group> <label class=control-label translate=reachoutApp.notification.delivered for=field_delivered>Delivered</label> <input class=form-control name=delivered id=field_delivered ng-model=\"notification.delivered\"/> </div> <div class=form-group> <label class=control-label translate=reachoutApp.notification.active for=field_active>Active</label> <input type=checkbox class=form-control name=active id=field_active ng-model=\"notification.active\"/> </div> <div class=form-group> <label class=control-label translate=reachoutApp.notification.created for=field_created>Created</label> <input id=field_created class=form-control uib-datepicker-popup={{dateformat}} ng-model=notification.created is-open=\"datePickerForCreated.status.opened\"/> <span class=input-group-btn> <button type=button class=\"btn btn-default\" ng-click=datePickerForCreatedOpen($event)><i class=\"glyphicon glyphicon-calendar\"></i></button> </span> </div> <div class=form-group> <label class=control-label translate=reachoutApp.notification.updated for=field_updated>Updated</label> <input id=field_updated class=form-control uib-datepicker-popup={{dateformat}} ng-model=notification.updated is-open=\"datePickerForUpdated.status.opened\"/> <span class=input-group-btn> <button type=button class=\"btn btn-default\" ng-click=datePickerForUpdatedOpen($event)><i class=\"glyphicon glyphicon-calendar\"></i></button> </span> </div> <div class=form-group> <label class=control-label translate=reachoutApp.notification.offensive for=field_offensive>Offensive</label> <input class=form-control name=offensive id=field_offensive ng-model=\"notification.offensive\"/> </div> <div class=form-group> <label class=control-label translate=reachoutApp.notification.publicId for=field_publicId>PublicId</label> <input class=form-control name=publicId id=field_publicId ng-model=\"notification.publicId\"/> </div> <div class=form-group> <label class=control-label translate=reachoutApp.notification.url for=field_url>Url</label> <input class=form-control name=url id=field_url ng-model=\"notification.url\"/> </div> <div class=form-group> <label class=control-label translate=reachoutApp.notification.consumerId for=field_consumerId>ConsumerId</label> <input class=form-control name=consumerId id=field_consumerId ng-model=\"notification.consumerId\"/> </div> </div> <div class=modal-footer> <button type=button class=\"btn btn-default\" data-dismiss=modal ng-click=clear()> <span class=\"glyphicon glyphicon-ban-circle\"></span>&nbsp;<span translate=entity.action.cancel>Cancel</span> </button> <button type=submit ng-disabled=\"editForm.$invalid || isSaving\" class=\"btn btn-primary\"> <span class=\"glyphicon glyphicon-save\"></span>&nbsp;<span translate=entity.action.save>Save</span> </button> </div> </form>"
  );


  $templateCache.put('scripts/app/entities/notification/notifications.html',
    "<div> <h2 translate=reachoutApp.notification.home.title>Notifications</h2> <jh-alert></jh-alert> <div class=container> <div class=row> <div class=col-md-4> <button class=\"btn btn-primary\" ui-sref=notification.new> <span class=\"glyphicon glyphicon-flash\"></span> <span translate=reachoutApp.notification.home.createLabel>Create a new Notification</span> </button> </div> </div> </div> <!-- Done by karthik --> <form> <div class=form-group> <div class=input-group> <div class=input-group-addon><i class=\"fa fa-search\"></i></div> <input class=form-control placeholder=\"Custom Filter......\" ng-model=search> </div> </div> </form> <!-- Done by karthik --> <div class=table-responsive> <table class=\"table table-striped\"> <thead> <tr> <th><span translate=global.field.id>ID</span></th> <th> <!-- <span translate=\"reachoutApp.notification.categery\"> --> <div class=btn-group data-ng-class=\"{open: open}\"> <button class=btn>Category</button> <button class=\"btn dropdown-toggle\" data-ng-click=\"open=!open\"> <span class=caret></span> </button> <ul class=dropdown-menu aria-labelledby=dropdownMenu> <li><a data-ng-click=checkAll()><i class=icon-ok-sign></i> Check All</a></li> <li><a data-ng-click=\"selectedNotifications=[];\"><i class=icon-remove-sign></i> Uncheck All</a></li> <li class=divider></li> <li data-ng-repeat=\"category in categoryList\"><a data-ng-click=setselectedNotification()>{{category.name}}<span data-ng-class=isChecked(category.name)></span></a></li> </ul> </div> </th> <th> <a ng-click=\"sortType = 'title'; sortReverse = !sortReverse\"> <span translate=reachoutApp.notification.title>Title</span> <span ng-show=\"sortType == 'title' && !sortReverse\" class=\"glyphicon glyphicon-triangle-bottom\"></span> <span ng-show=\"sortType == 'title' && sortReverse\" class=\"glyphicon glyphicon-triangle-top\"></span> </a> </th> <th><span translate=reachoutApp.notification.description>Description</span></th> <th><span translate=reachoutApp.notification.validFrom>ValidFrom</span></th> <th><span translate=reachoutApp.notification.validTo>ValidTo</span></th> <th><span translate=reachoutApp.notification.secureUrl>SecureUrl</span></th> <th><span translate=reachoutApp.notification.expieryDate>ExpieryDate</span></th> <th><span translate=reachoutApp.notification.delivered>Delivered</span></th> <th> <!-- <span translate=\"reachoutApp.notification.active\">Active</span> --> <select ng-model=search> <option value=\"\">Active</option> <option value=true>true</option> <option value=false>false</option> </select> </th> <th><span translate=reachoutApp.notification.created>Created</span></th> <th><span translate=reachoutApp.notification.updated>Updated</span></th> <th><span translate=reachoutApp.notification.offensive>Offensive</span></th> <th><span translate=reachoutApp.notification.publicId>PublicId</span></th> <th><span translate=reachoutApp.notification.url>Url</span></th> <th><span translate=reachoutApp.notification.consumerId>ConsumerId</span></th> <th></th> </tr> </thead> <tbody> <tr ng-repeat=\"notification in filtered = (notifications | notificationFilter:selectedNotifications) | filter:search | orderBy:sortType:sortReverse track by notification.id\"> <td><a ui-sref=notification.detail({id:notification.id})>{{notification.id}}</a></td> <td>{{notification.categery}}</td> <td><a ui-sref=notification.detail({id:notification.id})>{{notification.title}}</a></td> <td>{{notification.description}}</td> <td>{{notification.validFrom | date:'medium'}}</td> <td>{{notification.validTo | date:'medium'}}</td> <td>{{notification.secureUrl}}</td> <td>{{notification.expieryDate | date:'medium'}}</td> <td>{{notification.delivered}}</td> <td> <button type=submit ng-click=approve(notification.id,notification.active) class=\"btn btn-info btn-sm\"> <span class=\"glyphicon glyphicon-eye-open\"></span>&nbsp;<span> {{notification.active == true ? 'Approved' : 'Approval Pending'}}</span> </button> </td> <td>{{notification.created | date:'medium'}}</td> <td>{{notification.updated | date:'medium'}}</td> <td>{{notification.offensive}}</td> <td>{{notification.publicId}}</td> <td>{{notification.url}}</td> <td>{{notification.consumerId}}</td> <td> <button type=submit ui-sref=notification.detail({id:notification.id}) class=\"btn btn-info btn-sm\"> <span class=\"glyphicon glyphicon-eye-open\"></span>&nbsp;<span translate=entity.action.view> View</span> </button> <button type=submit ui-sref=notification.edit({id:notification.id}) class=\"btn btn-primary btn-sm\"> <span class=\"glyphicon glyphicon-pencil\"></span>&nbsp;<span translate=entity.action.edit> Edit</span> </button> <button type=submit ui-sref=notification.delete({id:notification.id}) class=\"btn btn-danger btn-sm\"> <span class=\"glyphicon glyphicon-remove-circle\"></span>&nbsp;<span translate=entity.action.delete> Delete</span> </button> </td> </tr> </tbody> </table> </div> <div class=text-center> <uib-pagination class=pagination-sm total-items=100 items-per-page=20 ng-model=page ng-change=loadAll()></uib-pagination> </div> </div>"
  );


  $templateCache.put('scripts/app/entities/notificationAcknowledgement/notificationAcknowledgement-delete-dialog.html',
    "<form name=deleteForm ng-submit=confirmDelete(notificationAcknowledgement.id)> <div class=modal-header> <button type=button class=close data-dismiss=modal aria-hidden=true ng-click=clear()>&times;</button> <h4 class=modal-title translate=entity.delete.title>Confirm delete operation</h4> </div> <div class=modal-body> <p translate=reachoutApp.notificationAcknowledgement.delete.question translate-values=\"{id: '{{notificationAcknowledgement.id}}'}\">Are you sure you want to delete this NotificationAcknowledgement?</p> </div> <div class=modal-footer> <button type=button class=\"btn btn-default\" data-dismiss=modal ng-click=clear()> <span class=\"glyphicon glyphicon-ban-circle\"></span>&nbsp;<span translate=entity.action.cancel>Cancel</span> </button> <button type=submit ng-disabled=deleteForm.$invalid class=\"btn btn-danger\"> <span class=\"glyphicon glyphicon-remove-circle\"></span>&nbsp;<span translate=entity.action.delete>Delete</span> </button> </div> </form>"
  );


  $templateCache.put('scripts/app/entities/notificationAcknowledgement/notificationAcknowledgement-detail.html',
    "<div> <h2><span translate=reachoutApp.notificationAcknowledgement.detail.title>NotificationAcknowledgement</span> {{notificationAcknowledgement.id}}</h2> <div class=table-responsive> <table class=\"table table-striped\"> <thead> <tr> <th translate=entity.detail.field>Field</th> <th translate=entity.detail.value>Value</th> </tr> </thead> <tbody> <tr> <td> <span translate=reachoutApp.notificationAcknowledgement.read>Read</span> </td> <td> <span class=form-control-static>{{notificationAcknowledgement.read}}</span> </td> </tr> <tr> <td> <span translate=reachoutApp.notificationAcknowledgement.consumerId>ConsumerId</span> </td> <td> <span class=form-control-static>{{notificationAcknowledgement.consumerId}}</span> </td> </tr> <tr> <td> <span translate=reachoutApp.notificationAcknowledgement.notificationId>NotificationId</span> </td> <td> <span class=form-control-static>{{notificationAcknowledgement.notificationId}}</span> </td> </tr> <tr> <td> <span translate=reachoutApp.notificationAcknowledgement.delivered>Delivered</span> </td> <td> <span class=form-control-static>{{notificationAcknowledgement.delivered}}</span> </td> </tr> </tbody> </table> </div> <button type=submit onclick=window.history.back() class=\"btn btn-info\"> <span class=\"glyphicon glyphicon-arrow-left\"></span>&nbsp;<span translate=entity.action.back> Back</span> </button> </div>"
  );


  $templateCache.put('scripts/app/entities/notificationAcknowledgement/notificationAcknowledgement-dialog.html',
    "<form name=editForm role=form novalidate ng-submit=save()> <div class=modal-header> <button type=button class=close data-dismiss=modal aria-hidden=true ng-click=clear()>&times;</button> <h4 class=modal-title id=myNotificationAcknowledgementLabel translate=reachoutApp.notificationAcknowledgement.home.createOrEditLabel>Create or edit a NotificationAcknowledgement</h4> </div> <div class=modal-body> <jh-alert-error></jh-alert-error> <div class=form-group> <label for=id translate=global.field.id>ID</label> <input class=form-control id=id name=id ng-model=notificationAcknowledgement.id readonly/> </div> <div class=form-group> <label class=control-label translate=reachoutApp.notificationAcknowledgement.read for=field_read>Read</label> <input class=form-control name=read id=field_read ng-model=\"notificationAcknowledgement.read\"/> </div> <div class=form-group> <label class=control-label translate=reachoutApp.notificationAcknowledgement.consumerId for=field_consumerId>ConsumerId</label> <input class=form-control name=consumerId id=field_consumerId ng-model=\"notificationAcknowledgement.consumerId\"/> </div> <div class=form-group> <label class=control-label translate=reachoutApp.notificationAcknowledgement.notificationId for=field_notificationId>NotificationId</label> <input class=form-control name=notificationId id=field_notificationId ng-model=\"notificationAcknowledgement.notificationId\"/> </div> <div class=form-group> <label class=control-label translate=reachoutApp.notificationAcknowledgement.delivered for=field_delivered>Delivered</label> <input class=form-control name=delivered id=field_delivered ng-model=\"notificationAcknowledgement.delivered\"/> </div> </div> <div class=modal-footer> <button type=button class=\"btn btn-default\" data-dismiss=modal ng-click=clear()> <span class=\"glyphicon glyphicon-ban-circle\"></span>&nbsp;<span translate=entity.action.cancel>Cancel</span> </button> <button type=submit ng-disabled=\"editForm.$invalid || isSaving\" class=\"btn btn-primary\"> <span class=\"glyphicon glyphicon-save\"></span>&nbsp;<span translate=entity.action.save>Save</span> </button> </div> </form>"
  );


  $templateCache.put('scripts/app/entities/notificationAcknowledgement/notificationAcknowledgements.html',
    "<div> <h2 translate=reachoutApp.notificationAcknowledgement.home.title>NotificationAcknowledgements</h2> <jh-alert></jh-alert> <div class=container> <div class=row> <div class=col-md-4> <button class=\"btn btn-primary\" ui-sref=notificationAcknowledgement.new> <span class=\"glyphicon glyphicon-flash\"></span> <span translate=reachoutApp.notificationAcknowledgement.home.createLabel>Create a new NotificationAcknowledgement</span> </button> </div> </div> </div> <div class=table-responsive> <table class=\"table table-striped\"> <thead> <tr> <th><span translate=global.field.id>ID</span></th> <th><span translate=reachoutApp.notificationAcknowledgement.read>Read</span></th> <th><span translate=reachoutApp.notificationAcknowledgement.consumerId>ConsumerId</span></th> <th><span translate=reachoutApp.notificationAcknowledgement.notificationId>NotificationId</span></th> <th><span translate=reachoutApp.notificationAcknowledgement.delivered>Delivered</span></th> <th></th> </tr> </thead> <tbody> <tr ng-repeat=\"notificationAcknowledgement in notificationAcknowledgements track by notificationAcknowledgement.id\"> <td><a ui-sref=notificationAcknowledgement.detail({id:notificationAcknowledgement.id})>{{notificationAcknowledgement.id}}</a></td> <td>{{notificationAcknowledgement.read}}</td> <td>{{notificationAcknowledgement.consumerId}}</td> <td>{{notificationAcknowledgement.notificationId}}</td> <td>{{notificationAcknowledgement.delivered}}</td> <td> <button type=submit ui-sref=notificationAcknowledgement.detail({id:notificationAcknowledgement.id}) class=\"btn btn-info btn-sm\"> <span class=\"glyphicon glyphicon-eye-open\"></span>&nbsp;<span translate=entity.action.view> View</span> </button> <button type=submit ui-sref=notificationAcknowledgement.edit({id:notificationAcknowledgement.id}) class=\"btn btn-primary btn-sm\"> <span class=\"glyphicon glyphicon-pencil\"></span>&nbsp;<span translate=entity.action.edit> Edit</span> </button> <button type=submit ui-sref=notificationAcknowledgement.delete({id:notificationAcknowledgement.id}) class=\"btn btn-danger btn-sm\"> <span class=\"glyphicon glyphicon-remove-circle\"></span>&nbsp;<span translate=entity.action.delete> Delete</span> </button> </td> </tr> </tbody> </table> </div> </div>"
  );


  $templateCache.put('scripts/app/entities/privateMessage/privateMessage-delete-dialog.html',
    "<form name=deleteForm ng-submit=confirmDelete(privateMessage.id)> <div class=modal-header> <button type=button class=close data-dismiss=modal aria-hidden=true ng-click=clear()>&times;</button> <h4 class=modal-title translate=entity.delete.title>Confirm delete operation</h4> </div> <div class=modal-body> <p translate=reachoutApp.privateMessage.delete.question translate-values=\"{id: '{{privateMessage.id}}'}\">Are you sure you want to delete this PrivateMessage?</p> </div> <div class=modal-footer> <button type=button class=\"btn btn-default\" data-dismiss=modal ng-click=clear()> <span class=\"glyphicon glyphicon-ban-circle\"></span>&nbsp;<span translate=entity.action.cancel>Cancel</span> </button> <button type=submit ng-disabled=deleteForm.$invalid class=\"btn btn-danger\"> <span class=\"glyphicon glyphicon-remove-circle\"></span>&nbsp;<span translate=entity.action.delete>Delete</span> </button> </div> </form>"
  );


  $templateCache.put('scripts/app/entities/privateMessage/privateMessage-detail.html',
    "<div> <h2><span translate=reachoutApp.privateMessage.detail.title>PrivateMessage</span> {{privateMessage.id}}</h2> <div class=table-responsive> <table class=\"table table-striped\"> <thead> <tr> <th translate=entity.detail.field>Field</th> <th translate=entity.detail.value>Value</th> </tr> </thead> <tbody> <tr> <td> <span translate=reachoutApp.privateMessage.message>Message</span> </td> <td> <span class=form-control-static>{{privateMessage.message}}</span> </td> </tr> <tr> <td> <span translate=reachoutApp.privateMessage.read>Read</span> </td> <td> <span class=form-control-static>{{privateMessage.read}}</span> </td> </tr> <tr> <td> <span translate=reachoutApp.privateMessage.delivered>Delivered</span> </td> <td> <span class=form-control-static>{{privateMessage.delivered}}</span> </td> </tr> <tr> <td> <span translate=reachoutApp.privateMessage.created>Created</span> </td> <td> <span class=form-control-static>{{privateMessage.created | date:'medium'}}</span> </td> </tr> <tr> <td> <span translate=reachoutApp.privateMessage.notificationId>NotificationId</span> </td> <td> <span class=form-control-static>{{privateMessage.notificationId}}</span> </td> </tr> <tr> <td> <span translate=reachoutApp.privateMessage.senderId>SenderId</span> </td> <td> <span class=form-control-static>{{privateMessage.senderId}}</span> </td> </tr> <tr> <td> <span translate=reachoutApp.privateMessage.receiverId>ReceiverId</span> </td> <td> <span class=form-control-static>{{privateMessage.receiverId}}</span> </td> </tr> </tbody> </table> </div> <button type=submit onclick=window.history.back() class=\"btn btn-info\"> <span class=\"glyphicon glyphicon-arrow-left\"></span>&nbsp;<span translate=entity.action.back> Back</span> </button> </div>"
  );


  $templateCache.put('scripts/app/entities/privateMessage/privateMessage-dialog.html',
    "<form name=editForm role=form novalidate ng-submit=save()> <div class=modal-header> <button type=button class=close data-dismiss=modal aria-hidden=true ng-click=clear()>&times;</button> <h4 class=modal-title id=myPrivateMessageLabel translate=reachoutApp.privateMessage.home.createOrEditLabel>Create or edit a PrivateMessage</h4> </div> <div class=modal-body> <jh-alert-error></jh-alert-error> <div class=form-group> <label for=id translate=global.field.id>ID</label> <input class=form-control id=id name=id ng-model=privateMessage.id readonly/> </div> <div class=form-group> <label class=control-label translate=reachoutApp.privateMessage.message for=field_message>Message</label> <input class=form-control name=message id=field_message ng-model=\"privateMessage.message\"/> </div> <div class=form-group> <label class=control-label translate=reachoutApp.privateMessage.read for=field_read>Read</label> <input type=checkbox class=form-control name=read id=field_read ng-model=\"privateMessage.read\"/> </div> <div class=form-group> <label class=control-label translate=reachoutApp.privateMessage.delivered for=field_delivered>Delivered</label> <input class=form-control name=delivered id=field_delivered ng-model=\"privateMessage.delivered\"/> </div> <div class=form-group> <label class=control-label translate=reachoutApp.privateMessage.created for=field_created>Created</label> <input id=field_created class=form-control uib-datepicker-popup={{dateformat}} ng-model=privateMessage.created is-open=\"datePickerForCreated.status.opened\"/> <span class=input-group-btn> <button type=button class=\"btn btn-default\" ng-click=datePickerForCreatedOpen($event)><i class=\"glyphicon glyphicon-calendar\"></i></button> </span> </div> <div class=form-group> <label class=control-label translate=reachoutApp.privateMessage.notificationId for=field_notificationId>NotificationId</label> <input class=form-control name=notificationId id=field_notificationId ng-model=\"privateMessage.notificationId\"/> </div> <div class=form-group> <label class=control-label translate=reachoutApp.privateMessage.senderId for=field_senderId>SenderId</label> <input class=form-control name=senderId id=field_senderId ng-model=\"privateMessage.senderId\"/> </div> <div class=form-group> <label class=control-label translate=reachoutApp.privateMessage.receiverId for=field_receiverId>ReceiverId</label> <input class=form-control name=receiverId id=field_receiverId ng-model=\"privateMessage.receiverId\"/> </div> </div> <div class=modal-footer> <button type=button class=\"btn btn-default\" data-dismiss=modal ng-click=clear()> <span class=\"glyphicon glyphicon-ban-circle\"></span>&nbsp;<span translate=entity.action.cancel>Cancel</span> </button> <button type=submit ng-disabled=\"editForm.$invalid || isSaving\" class=\"btn btn-primary\"> <span class=\"glyphicon glyphicon-save\"></span>&nbsp;<span translate=entity.action.save>Save</span> </button> </div> </form>"
  );


  $templateCache.put('scripts/app/entities/privateMessage/privateMessages.html',
    "<div> <h2 translate=reachoutApp.privateMessage.home.title>PrivateMessages</h2> <jh-alert></jh-alert> <div class=container> <div class=row> <div class=col-md-4> <button class=\"btn btn-primary\" ui-sref=privateMessage.new> <span class=\"glyphicon glyphicon-flash\"></span> <span translate=reachoutApp.privateMessage.home.createLabel>Create a new PrivateMessage</span> </button> </div> </div> </div> <div class=table-responsive> <table class=\"table table-striped\"> <thead> <tr> <th><span translate=global.field.id>ID</span></th> <th><span translate=reachoutApp.privateMessage.message>Message</span></th> <th><span translate=reachoutApp.privateMessage.read>Read</span></th> <th><span translate=reachoutApp.privateMessage.delivered>Delivered</span></th> <th><span translate=reachoutApp.privateMessage.created>Created</span></th> <th><span translate=reachoutApp.privateMessage.notificationId>NotificationId</span></th> <th><span translate=reachoutApp.privateMessage.senderId>SenderId</span></th> <th><span translate=reachoutApp.privateMessage.receiverId>ReceiverId</span></th> <th></th> </tr> </thead> <tbody> <tr ng-repeat=\"privateMessage in privateMessages track by privateMessage.id\"> <td><a ui-sref=privateMessage.detail({id:privateMessage.id})>{{privateMessage.id}}</a></td> <td>{{privateMessage.message}}</td> <td>{{privateMessage.read}}</td> <td>{{privateMessage.delivered}}</td> <td>{{privateMessage.created | date:'medium'}}</td> <td>{{privateMessage.notificationId}}</td> <td>{{privateMessage.senderId}}</td> <td>{{privateMessage.receiverId}}</td> <td> <button type=submit ui-sref=privateMessage.detail({id:privateMessage.id}) class=\"btn btn-info btn-sm\"> <span class=\"glyphicon glyphicon-eye-open\"></span>&nbsp;<span translate=entity.action.view> View</span> </button> <button type=submit ui-sref=privateMessage.edit({id:privateMessage.id}) class=\"btn btn-primary btn-sm\"> <span class=\"glyphicon glyphicon-pencil\"></span>&nbsp;<span translate=entity.action.edit> Edit</span> </button> <button type=submit ui-sref=privateMessage.delete({id:privateMessage.id}) class=\"btn btn-danger btn-sm\"> <span class=\"glyphicon glyphicon-remove-circle\"></span>&nbsp;<span translate=entity.action.delete> Delete</span> </button> </td> </tr> </tbody> </table> </div> </div>"
  );


  $templateCache.put('scripts/app/entities/region/region-delete-dialog.html',
    "<form name=deleteForm ng-submit=confirmDelete(region.id)> <div class=modal-header> <button type=button class=close data-dismiss=modal aria-hidden=true ng-click=clear()>&times;</button> <h4 class=modal-title translate=entity.delete.title>Confirm delete operation</h4> </div> <div class=modal-body> <p translate=reachoutApp.region.delete.question translate-values=\"{id: '{{region.id}}'}\">Are you sure you want to delete this Region?</p> </div> <div class=modal-footer> <button type=button class=\"btn btn-default\" data-dismiss=modal ng-click=clear()> <span class=\"glyphicon glyphicon-ban-circle\"></span>&nbsp;<span translate=entity.action.cancel>Cancel</span> </button> <button type=submit ng-disabled=deleteForm.$invalid class=\"btn btn-danger\"> <span class=\"glyphicon glyphicon-remove-circle\"></span>&nbsp;<span translate=entity.action.delete>Delete</span> </button> </div> </form>"
  );


  $templateCache.put('scripts/app/entities/region/region-detail.html',
    "<div> <h2><span translate=reachoutApp.region.detail.title>Region</span> {{region.id}}</h2> <div class=table-responsive> <table class=\"table table-striped\"> <thead> <tr> <th translate=entity.detail.field>Field</th> <th translate=entity.detail.value>Value</th> </tr> </thead> <tbody> <tr> <td> <span translate=reachoutApp.region.name>Name</span> </td> <td> <span class=form-control-static>{{region.name}}</span> </td> </tr> <tr> <td> <span translate=reachoutApp.region.location>Location</span> </td> <td> <span class=form-control-static>{{region.location}}</span> </td> </tr> <tr> <td> <span translate=reachoutApp.region.created>Created</span> </td> <td> <span class=form-control-static>{{region.created | date:'medium'}}</span> </td> </tr> </tbody> </table> </div> <button type=submit onclick=window.history.back() class=\"btn btn-info\"> <span class=\"glyphicon glyphicon-arrow-left\"></span>&nbsp;<span translate=entity.action.back> Back</span> </button> </div>"
  );


  $templateCache.put('scripts/app/entities/region/region-dialog.html',
    "<form name=editForm role=form novalidate ng-submit=save()> <div class=modal-header> <button type=button class=close data-dismiss=modal aria-hidden=true ng-click=clear()>&times;</button> <h4 class=modal-title id=myRegionLabel translate=reachoutApp.region.home.createOrEditLabel>Create or edit a Region</h4> </div> <div class=modal-body> <jh-alert-error></jh-alert-error> <div class=form-group> <label for=id translate=global.field.id>ID</label> <input class=form-control id=id name=id ng-model=region.id readonly/> </div> <div class=form-group> <label class=control-label translate=reachoutApp.region.name for=field_name>Name</label> <input class=form-control name=name id=field_name ng-model=\"region.name\"/> </div> <div class=form-group> <label class=control-label translate=reachoutApp.region.location for=field_location>Location</label> <input class=form-control name=location id=field_location ng-model=\"region.location\"/> </div> <div class=form-group> <label class=control-label translate=reachoutApp.region.created for=field_created>Created</label> <input id=field_created class=form-control uib-datepicker-popup={{dateformat}} ng-model=region.created is-open=\"datePickerForCreated.status.opened\"/> <span class=input-group-btn> <button type=button class=\"btn btn-default\" ng-click=datePickerForCreatedOpen($event)><i class=\"glyphicon glyphicon-calendar\"></i></button> </span> </div> </div> <div class=modal-footer> <button type=button class=\"btn btn-default\" data-dismiss=modal ng-click=clear()> <span class=\"glyphicon glyphicon-ban-circle\"></span>&nbsp;<span translate=entity.action.cancel>Cancel</span> </button> <button type=submit ng-disabled=\"editForm.$invalid || isSaving\" class=\"btn btn-primary\"> <span class=\"glyphicon glyphicon-save\"></span>&nbsp;<span translate=entity.action.save>Save</span> </button> </div> </form>"
  );


  $templateCache.put('scripts/app/entities/region/regions.html',
    "<div> <h2 translate=reachoutApp.region.home.title>Regions</h2> <jh-alert></jh-alert> <div class=container> <div class=row> <div class=col-md-4> <button class=\"btn btn-primary\" ui-sref=region.new> <span class=\"glyphicon glyphicon-flash\"></span> <span translate=reachoutApp.region.home.createLabel>Create a new Region</span> </button> </div> </div> </div> <div class=table-responsive> <table class=\"table table-striped\"> <thead> <tr> <th><span translate=global.field.id>ID</span></th> <th><span translate=reachoutApp.region.name>Name</span></th> <th><span translate=reachoutApp.region.location>Location</span></th> <th><span translate=reachoutApp.region.created>Created</span></th> <th></th> </tr> </thead> <tbody> <tr ng-repeat=\"region in regions track by region.id\"> <td><a ui-sref=region.detail({id:region.id})>{{region.id}}</a></td> <td>{{region.name}}</td> <td>{{region.location}}</td> <td>{{region.created | date:'medium'}}</td> <td> <button type=submit ui-sref=region.detail({id:region.id}) class=\"btn btn-info btn-sm\"> <span class=\"glyphicon glyphicon-eye-open\"></span>&nbsp;<span translate=entity.action.view> View</span> </button> <button type=submit ui-sref=region.edit({id:region.id}) class=\"btn btn-primary btn-sm\"> <span class=\"glyphicon glyphicon-pencil\"></span>&nbsp;<span translate=entity.action.edit> Edit</span> </button> <button type=submit ui-sref=region.delete({id:region.id}) class=\"btn btn-danger btn-sm\"> <span class=\"glyphicon glyphicon-remove-circle\"></span>&nbsp;<span translate=entity.action.delete> Delete</span> </button> </td> </tr> </tbody> </table> </div> </div>"
  );


  $templateCache.put('scripts/app/error/accessdenied.html',
    "<div ng-cloak> <div class=row> <div class=col-md-4> <span class=\"hipster img-responsive img-rounded\"></span> </div> <div class=col-md-8> <h1 translate=error.title>Error Page!</h1> <div class=\"alert alert-danger\" translate=error.403>You are not authorized to access the page. </div> </div> </div> </div>"
  );


  $templateCache.put('scripts/app/error/error.html',
    "<div ng-cloak> <div class=row> <div class=col-md-4> <span class=\"hipster img-responsive img-rounded\"></span> </div> <div class=col-md-8> <h1 translate=error.title>Error Page!</h1> <div ng-show=errorMessage> <div class=\"alert alert-danger\">{{errorMessage}} </div> </div> </div> </div> </div>"
  );


  $templateCache.put('scripts/app/main/main.html',
    "<div ng-cloak> <div class=row> <div class=col-md-4> <span class=\"hipster img-responsive img-rounded\"></span> </div> <div class=col-md-8> <h1 translate=main.title>Welcome, Java Hipster!</h1> <p class=lead translate=main.subtitle>This is your homepage</p> <div ng-switch=isAuthenticated()> <div class=\"alert alert-success\" ng-switch-when=true translate=main.logged.message translate-values=\"{username: '{{account.login}}'}\"> You are logged in as user \"{{account.login}}\". </div> <div class=\"alert alert-warning\" ng-switch-when=false translate=global.messages.info.authenticated> If you want to <a class=alert-link href=#/login>sign in</a>, you can try the default accounts:<br/>- Administrator (login=\"admin\" and password=\"admin\") <br/>- User (login=\"user\" and password=\"user\"). </div> <div class=\"alert alert-warning\" ng-switch-when=false translate=global.messages.info.register> You don't have an account yet? <a class=alert-link href=#/register>Register a new account</a> </div> </div> <p translate=main.question> If you have any question on JHipster: </p> <ul> <li><a href=\"http://jhipster.github.io/\" target=_blank translate=main.link.homepage>JHipster homepage</a></li> <li><a href=http://stackoverflow.com/tags/jhipster/info target=_blank translate=main.link.stackoverflow>JHipster on Stack Overflow</a></li> <li><a href=\"https://github.com/jhipster/generator-jhipster/issues?state=open\" target=_blank translate=main.link.bugtracker>JHipster bug tracker</a></li> <li><a href=https://gitter.im/jhipster/generator-jhipster target=_blank translate=main.link.chat>JHipster public chat room</a></li> <li><a href=https://twitter.com/java_hipster target=_blank translate=main.link.contact>contact @java_hipster on Twitter</a></li> </ul> <p> <span translate=main.like>If you like JHipster, don't forget to give us a star on </span>&nbsp;<a href=https://github.com/jhipster/generator-jhipster target=_blank translate=main.github>Github</a>! </p> </div> </div> </div>"
  );


  $templateCache.put('scripts/components/navbar/navbar.html',
    "<nav class=\"navbar navbar-default\" role=navigation> <div class=container> <div class=navbar-header> <button type=button class=navbar-toggle data-toggle=collapse data-target=#navbar-collapse> <span class=sr-only>Toggle navigation</span> <span class=icon-bar></span> <span class=icon-bar></span> <span class=icon-bar></span> </button> <a class=navbar-brand href=\"#/\"><span translate=global.title>jhipster</span> <span class=navbar-version>v{{VERSION}}</span></a> </div> <div class=\"collapse navbar-collapse\" id=navbar-collapse ng-switch=isAuthenticated()> <ul class=\"nav navbar-nav navbar-right\"> <li ui-sref-active=active> <a ui-sref=home data-toggle=collapse data-target=.navbar-collapse.in> <span class=\"glyphicon glyphicon-home\"></span> <span class=hidden-sm translate=global.menu.home>Home</span> </a> </li> <!-- Changed by karthik --> <li ui-sref-active=active ng-switch-when=true><a ui-sref=notification data-toggle=collapse data-target=.navbar-collapse.in> <span class=\"glyphicon glyphicon-asterisk\"></span> &#xA0;<span translate=global.menu.entities.notification>notification</span> </a></li> <li ui-sref-active=active ng-switch-when=true><a ui-sref=consumer data-toggle=collapse data-target=.navbar-collapse.in> <span class=\"glyphicon glyphicon-asterisk\"></span> &#xA0;<span translate=global.menu.entities.consumer>consumer</span> </a></li> <!-- End by karthik --> <!-- jhipster-needle-add-element-to-menu - JHipster will add new menu items here --> <li ui-sref-active=active ng-switch-when=true class=\"dropdown pointer\"> <a class=dropdown-toggle data-toggle=dropdown href=\"\"> <span> <span class=\"glyphicon glyphicon-th-list\"></span> <span class=hidden-sm translate=global.menu.entities.main> Entities </span> <b class=caret></b> </span> </a> <ul class=dropdown-menu> <li ui-sref-active=active><a ui-sref=notification data-toggle=collapse data-target=.navbar-collapse.in><span class=\"glyphicon glyphicon-asterisk\"></span> &#xA0;<span translate=global.menu.entities.notification>notification</span></a></li> <li ui-sref-active=active><a ui-sref=cloudinary data-toggle=collapse data-target=.navbar-collapse.in><span class=\"glyphicon glyphicon-asterisk\"></span> &#xA0;<span translate=global.menu.entities.cloudinary>cloudinary</span></a></li> <li ui-sref-active=active><a ui-sref=consumerFavourite data-toggle=collapse data-target=.navbar-collapse.in><span class=\"glyphicon glyphicon-asterisk\"></span> &#xA0;<span translate=global.menu.entities.consumerFavourite>consumerFavourite</span></a></li> <li ui-sref-active=active><a ui-sref=consumerFeedback data-toggle=collapse data-target=.navbar-collapse.in><span class=\"glyphicon glyphicon-asterisk\"></span> &#xA0;<span translate=global.menu.entities.consumerFeedback>consumerFeedback</span></a></li> <li ui-sref-active=active><a ui-sref=consumerRegions data-toggle=collapse data-target=.navbar-collapse.in><span class=\"glyphicon glyphicon-asterisk\"></span> &#xA0;<span translate=global.menu.entities.consumerRegions>consumerRegions</span></a></li> <li ui-sref-active=active><a ui-sref=region data-toggle=collapse data-target=.navbar-collapse.in><span class=\"glyphicon glyphicon-asterisk\"></span> &#xA0;<span translate=global.menu.entities.region>region</span></a></li> <li ui-sref-active=active><a ui-sref=notificationAcknowledgement data-toggle=collapse data-target=.navbar-collapse.in><span class=\"glyphicon glyphicon-asterisk\"></span> &#xA0;<span translate=global.menu.entities.notificationAcknowledgement>notificationAcknowledgement</span></a></li> <li ui-sref-active=active><a ui-sref=privateMessage data-toggle=collapse data-target=.navbar-collapse.in><span class=\"glyphicon glyphicon-asterisk\"></span> &#xA0;<span translate=global.menu.entities.privateMessage>privateMessage</span></a></li> <li ui-sref-active=active><a ui-sref=deviceInfo data-toggle=collapse data-target=.navbar-collapse.in><span class=\"glyphicon glyphicon-asterisk\"></span> &#xA0;<span translate=global.menu.entities.deviceInfo>deviceInfo</span></a></li> <li ui-sref-active=active><a ui-sref=consumer data-toggle=collapse data-target=.navbar-collapse.in><span class=\"glyphicon glyphicon-asterisk\"></span> &#xA0;<span translate=global.menu.entities.consumer>consumer</span></a></li> <!-- jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here --> </ul> </li> <li ng-class=\"{active: $state.includes('account')}\" class=\"dropdown pointer\"> <a class=dropdown-toggle data-toggle=dropdown href=\"\" id=account-menu> <span> <span class=\"glyphicon glyphicon-user\"></span> <span class=hidden-sm translate=global.menu.account.main> Account </span> <b class=caret></b> </span> </a> <ul class=dropdown-menu> <li ui-sref-active=active ng-switch-when=true><a ui-sref=settings data-toggle=collapse data-target=.navbar-collapse.in><span class=\"glyphicon glyphicon-wrench\"></span> &#xA0;<span translate=global.menu.account.settings>Settings</span></a></li> <li ui-sref-active=active ng-switch-when=true><a ui-sref=password data-toggle=collapse data-target=.navbar-collapse.in><span class=\"glyphicon glyphicon-lock\"></span> &#xA0;<span translate=global.menu.account.password>Password</span></a></li> <li ui-sref-active=active ng-switch-when=true><a href=\"\" ng-click=logout() id=logout data-toggle=collapse data-target=.navbar-collapse.in><span class=\"glyphicon glyphicon-log-out\"></span> &#xA0;<span translate=global.menu.account.logout>Sign out</span></a></li> <li ui-sref-active=active ng-switch-when=false><a ui-sref=login data-toggle=collapse data-target=.navbar-collapse.in><span class=\"glyphicon glyphicon-log-in\"></span> &#xA0;<span translate=global.menu.account.login>Sign in</span></a></li> <li ui-sref-active=active ng-switch-when=false><a ui-sref=register data-toggle=collapse data-target=.navbar-collapse.in><span class=\"glyphicon glyphicon-plus-sign\"></span> &#xA0;<span translate=global.menu.account.register>Register</span></a></li> </ul> </li> <li ng-class=\"{active: $state.includes('admin')}\" ng-switch-when=true has-authority=ROLE_ADMIN class=\"dropdown pointer\"> <a class=dropdown-toggle data-toggle=dropdown href=\"\" id=admin-menu> <span> <span class=\"glyphicon glyphicon-tower\"></span> <span class=hidden-sm translate=global.menu.admin.main>Administration</span> <b class=caret></b> </span> </a> <ul class=dropdown-menu> <li ui-sref-active=active><a ui-sref=user-management data-toggle=collapse data-target=.navbar-collapse.in><span class=\"glyphicon glyphicon-user\"></span> &#xA0;<span translate=global.menu.admin.user-management>User management</span></a></li> <li ui-sref-active=active><a ui-sref=tracker data-toggle=collapse data-target=.navbar-collapse.in><span class=\"glyphicon glyphicon-eye-open\"></span> &nbsp;<span translate=global.menu.admin.tracker>User tracker</span></a></li> <li ui-sref-active=active><a ui-sref=metrics data-toggle=collapse data-target=.navbar-collapse.in><span class=\"glyphicon glyphicon-dashboard\"></span> &#xA0;<span translate=global.menu.admin.metrics>Metrics</span></a></li> <li ui-sref-active=active><a ui-sref=health data-toggle=collapse data-target=.navbar-collapse.in><span class=\"glyphicon glyphicon-heart\"></span> &#xA0;<span translate=global.menu.admin.health>Health</span></a></li> <li ui-sref-active=active><a ui-sref=configuration data-toggle=collapse data-target=.navbar-collapse.in><span class=\"glyphicon glyphicon-list-alt\"></span> &#xA0;<span translate=global.menu.admin.configuration>Configuration</span></a></li> <li ui-sref-active=active><a ui-sref=audits data-toggle=collapse data-target=.navbar-collapse.in><span class=\"glyphicon glyphicon-bell\"></span> &#xA0;<span translate=global.menu.admin.audits>Audits</span></a></li> <li ui-sref-active=active><a ui-sref=logs data-toggle=collapse data-target=.navbar-collapse.in><span class=\"glyphicon glyphicon-tasks\"></span> &#xA0;<span translate=global.menu.admin.logs>Logs</span></a></li> <li ng-hide=inProduction ui-sref-active=active><a ui-sref=docs data-toggle=collapse data-target=.navbar-collapse.in><span class=\"glyphicon glyphicon-book\"></span> &#xA0;<span translate=global.menu.admin.apidocs>API</span></a></li> </ul> </li> <li ui-sref-active=active class=\"dropdown pointer\" ng-controller=LanguageController> <a class=dropdown-toggle data-toggle=dropdown href=\"\"> <span> <span class=\"glyphicon glyphicon-flag\"></span> <span class=hidden-sm translate=global.menu.language>Language</span> <b class=caret></b> </span> </a> <ul class=dropdown-menu> <li active-menu={{language}} ng-repeat=\"language in languages\"> <a href=\"\" ng-click=changeLanguage(language) data-toggle=collapse data-target=.navbar-collapse.in>{{language | findLanguageFromKey}}</a> </li> </ul> </li> </ul> </div> </div> </nav>"
  );

}]);
