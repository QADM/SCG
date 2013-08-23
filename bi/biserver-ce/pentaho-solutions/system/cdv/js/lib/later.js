/**
* Later.js 0.0.1
* (c) 2012 Bill, BunKat LLC.
* Later is freely distributable under the MIT license.
* For all details and documentation:
*     http://bunkat.github.com/later
*/
(function (window) {

    "use strict";

    /**
    * Parses a cron expression and produces a schedule that is compatible
    * with Later.js.  See http://en.wikipedia.org/wiki/Cron for details of
    * the cron format.
    */
    var CronParser = function () {
    
        // Constant array to convert valid names to values
        var NAMES = {
            JAN: 1, FEB: 2, MAR: 3, APR: 4, MAY: 5, JUN: 6, JUL: 7, AUG: 8,
            SEP: 9, OCT: 10, NOV: 11, DEC: 12,
            SUN: 1, MON: 2, TUE: 3, WED: 4, THU: 5, FRI: 6, SAT: 7
        };

        // Contains the index, min, and max for each of the constraints
        var FIELDS = {
            s: [0, 0, 59],      // seconds
            m: [1, 0, 59],      // minutes
            h: [2, 0, 23],      // hours
            D: [3, 1, 31],      // day of month
            M: [4, 1, 12],  // month
            Y: [6, 1970, 2099], // year
            d: [5, 1, 7, 1]        // day of week
        };

        /**
        * Returns the value + offset if value is a number, otherwise it
        * attempts to look up the value in the NAMES table and returns
        * that result instead. 
        *
        * @param {Int,String} value: The value that should be parsed
        * @param {Int} offset: Any offset that must be added to the value
        */
        var getValue = function(value, offset) {
            return isNaN(value) ? NAMES[value] : +value + (offset || 0);
        };

        /**
        * Returns a deep clone of a schedule skipping any day of week
        * constraints.
        *
        * @param {Sched} sched: The schedule that will be cloned
        */
        var cloneSchedule = function(sched) {
            var clone = {}, field;

            for(field in sched) {
                if (field !== 'dc' && field !== 'd') {
                    clone[field] = sched[field].slice(0);
                }
            }

            return clone;
        };

        /**
        * Adds values to the specified constraint in the current schedule.
        *
        * @param {Sched} sched: The schedule to add the constraint to
        * @param {String} name: Name of constraint to add
        * @param {Int} min: Minimum value for this constraint
        * @param {Int} max: Maximum value for this constraint
        * @param {Int} inc: The increment to use between min and max
        */
        var add = function (sched, name, min, max, inc) {
            var i = min;

            if (!sched[name]) {
                sched[name] = [];
            }

            while (i <= max) {
                if (sched[name].indexOf(i) < 0) {
                    sched[name].push(i);
                } 
                i += inc || 1;              
            }       
        };

        /**
        * Adds a hash item (of the form x#y or xL) to the schedule.  
        *
        * @param {Schedule} schedules: The current schedule array to add to
        * @param {Schedule} curSched: The current schedule to add to
        * @param {Int} value: The value to add (x of x#y or xL)
        * @param {Int} hash: The hash value to add (y of x#y)
        */
        var addHash = function(schedules, curSched, value, hash) {
            // if there are any existing day of week constraints that
            // aren't equal to the one we're adding, create a new
            // composite schedule
            if ((curSched.d && !curSched.dc) || 
                    (curSched.dc && curSched.dc.indexOf(hash) < 0)) {
                schedules.push(cloneSchedule(curSched));
                curSched = schedules[schedules.length-1];
            }

            add(curSched, 'd', value, value);
            add(curSched, 'dc', hash, hash);          
        };


        var addWeekday = function(s, curSched, value) {
             var except1 = {}, except2 = {};
             if (value=== 1) {
                // cron doesn't pass month boundaries, so if 1st is a
                // weekend then we need to use 2nd or 3rd instead
                add(curSched, 'D', 1, 3);
                add(curSched, 'd', NAMES.MON, NAMES.FRI);
                add(except1, 'D', 2, 2);
                add(except1, 'd', NAMES.TUE, NAMES.FRI); 
                add(except2, 'D', 3, 3);
                add(except2, 'd', NAMES.TUE, NAMES.FRI); 
            } else {
                // normally you want the closest day, so if v is a
                // Saturday, use the previous Friday.  If it's a
                // sunday, use the following Monday.
                add(curSched, 'D', value-1, value+1);
                add(curSched, 'd', NAMES.MON, NAMES.FRI);
                add(except1, 'D', value-1, value-1);
                add(except1, 'd', NAMES.MON, NAMES.THU); 
                add(except2, 'D', value+1, value+1);
                add(except2, 'd', NAMES.TUE, NAMES.FRI);                 
            }
            s.exceptions.push(except1);
            s.exceptions.push(except2);           
        };

        /**
        * Adds a range item (of the form x-y/z) to the schedule.  
        *
        * @param {String} item: The cron expression item to add
        * @param {Schedule} curSched: The current schedule to add to
        * @param {String} name: The name to use for this constraint
        * @param {Int} min: The min value for the constraint
        * @param {Int} max: The max value for the constraint
        * @param {Int} offset: The offset to apply to the cron value
        */
        var addRange = function(item, curSched, name, min, max, offset) {
            // parse range/x
            var incSplit = item.split('/')
              , inc = +incSplit[1]
              , range = incSplit[0];

            // parse x-y or * or 0
            if (range !== '*' && range !== '0') {
                var rangeSplit = range.split('-');
                min = getValue(rangeSplit[0], offset);
                max = getValue(rangeSplit[1], offset);
            }
            add(curSched, name, min, max, inc);           
        };

        /**
        * Parses a particular item within a cron expression.  
        *
        * @param {String} item: The cron expression item to parse
        * @param {Schedule} s: The existing set of schedules
        * @param {String} name: The name to use for this constraint
        * @param {Int} min: The min value for the constraint
        * @param {Int} max: The max value for the constraint
        * @param {Int} offset: The offset to apply to the cron value
        */
        var parse = function(item, s, name, min, max, offset) {
            var value
              , split
              , schedules = s.schedules
              , curSched = schedules[schedules.length-1];

            // L just means min - 1 (this also makes it work for any field)
            if (item === 'L') {
                item = min - 1;
            }
            
            // parse x
            if ((value = getValue(item, offset)) != null) {
                add(curSched, name, value, value);
            }
            // parse xW
            else if ((value = getValue(item.replace('W', ''), offset)) != null) {
                addWeekday(s, curSched, value);
            }
            // parse xL
            else if ((value = getValue(item.replace('L', ''), offset)) != null) {
                addHash(schedules, curSched, value, min-1);
            }
            // parse x#y
            else if ((split = item.split('#')).length === 2) {
                value = getValue(split[0], offset);
                addHash(schedules, curSched, value, getValue(split[1]));
            }
            // parse x-y or x-y/z or */z or 0/z 
            else {
                addRange(item, curSched, name, min, max, offset);
            }
            
        };

        /**
        * Returns true if the item is either of the form x#y or xL.
        *
        * @param {String} item: The expression item to check
        */
        var isHash = function(item) {
            return item.indexOf('#') > -1 || item.indexOf('L') > 0;
        };


        var itemSorter = function(a,b) {
          return isHash(a) && !isHash(b) ? 1 : 0; 
        };

        /**
        * Parses each of the fields in a cron expression.  The expression must
        * include the seconds field, the year field is optional.
        *
        * @param {String} expr: The cron expression to parse
        */
        var parseExpr = function(expr) {
            var schedule = {schedules: [{}], exceptions: []}   
              , components = expr.split(' ')
              , field, f, component, items;

            for(field in FIELDS) {
                f = FIELDS[field];
                component = components[f[0]];
                if (component && component !== '*' && component !== '?') {
                    // need to sort so that any #'s come last, otherwise
                    // schedule clones to handle # won't contain all of the 
                    // other constraints
                    items = component.split(',').sort(itemSorter);
                    var i, length = items.length;
                    for (i = 0; i < length; i++) {    
                        parse(items[i], schedule, field, f[1], f[2], f[3]);
                    }
                }
            }

            return schedule;
        };

        return {

            /**
            * Parses a valid cron expression and produces a valid schedule that
            * can then be used with Later.
            *
            * CronParser().parse('* 5 * * * * *', true);
            *
            * @param {String} expr: The cron expression to parse
            * @param {Bool} hasSeconds: True if the expression uses a seconds field
            * @api public
            */
            parse: function (expr, hasSeconds) { 
                var e = expr.toUpperCase();
                return parseExpr(hasSeconds ? e : '0 ' + e); 
            }

        };
    };

    /**
    * Allow library to be used within both the browser and node.js
    */
    var root = typeof exports !== "undefined" && exports !== null ? exports : window;
    root.cronParser = CronParser;  

}).call(this,this);/**
* Later.js 0.0.1
* (c) 2012 Bill, BunKat LLC.
* Later is freely distributable under the MIT license.
* For all details and documentation:
*     http://bunkat.github.com/later
*/
(function(window){
  var root =  typeof exports !== "undefined" && exports !== null ? exports : window;

  var recur = root.recur;
  if (!recur && (typeof require !== 'undefined')) {
      recur = require('./recur').recur;
  }
}).call(this,this);

(function (window) {

    "use strict";
    /**
    * Parses an English string expression and produces a schedule that is 
    * compatible with Later.js.  
    *
    * Examples:
    *
    * every 5 minutes between the 1st and 30th minute
    * at 10:00 am on tues of may in 2012
    * on the 15-20th day of march-dec
    * every 20 seconds every 5 minutes every 4 hours between the 10th and 20th hour
    */
    var EnParser = function () {
    
        var pos = 0
          , input = ''
          , error;

        // Regex expressions for all of the valid tokens
        var TOKENTYPES = {
          eof: /^$/,
          rank: /^((\d\d\d\d)|([2-5]?1(st)?|[2-5]?2(nd)?|[2-5]?3(rd)?|(0|[1-5]?[4-9]|[1-5]0|1[1-3])(th)?))\b/,
          time: /^((([0]?[1-9]|1[0-2]):[0-5]\d(\s)?(am|pm))|(([0]?\d|1\d|2[0-3]):[0-5]\d))\b/,
          dayName: /^((sun|mon|tue(s)?|wed(nes)?|thu(r(s)?)?|fri|sat(ur)?)(day)?)\b/,
          monthName: /^(jan(uary)?|feb(ruary)?|ma((r(ch)?)?|y)|apr(il)?|ju(ly|ne)|aug(ust)?|oct(ober)?|(sept|nov|dec)(ember)?)\b/,
          yearIndex: /^(\d\d\d\d)\b/,
          every: /^every\b/,
          after: /^after\b/,
          second: /^(s|sec(ond)?(s)?)\b/,
          minute: /^(m|min(ute)?(s)?)\b/,
          hour: /^(h|hour(s)?)\b/,
          day: /^(day(s)?( of the month)?)\b/,
          dayInstance: /^day instance\b/,
          dayOfWeek: /^day(s)? of the week\b/,
          dayOfYear: /^day(s)? of the year\b/,
          weekOfYear: /^week(s)?( of the year)?\b/,
          weekOfMonth: /^week(s)? of the month\b/,
          weekday: /^weekday\b/,
          weekend: /^weekend\b/,
          month: /^month(s)?\b/,
          year: /^year(s)?\b/,
          between: /^between (the)?\b/,
          start: /^(start(ing)? (at|on( the)?)?)\b/,
          at: /^(at|@)\b/,
          and: /^(,|and\b)/,
          except: /^(except\b)/,
          also: /(also)\b/,
          first: /^(first)\b/,
          last: /^last\b/,
          "in": /^in\b/,
          of: /^of\b/,
          onthe: /^on the\b/,
          on: /^on\b/,
          through: /(-|^(to|through)\b)/
        };

        // Array to convert string names to valid numerical values
        var NAMES = { jan: 1, feb: 2, mar: 3, apr: 4, may: 5, jun: 6, jul: 7, 
            aug: 8, sep: 9, oct: 10, nov: 11, dec: 12, sun: 1, mon: 2, tue: 3, 
            wed: 4, thu: 5, fri: 6, sat: 7, '1st': 1, fir: 1, '2nd': 2, sec: 2, 
            '3rd': 3, thi: 3, '4th': 4, 'for': 4  
        };

        /**
        * Bundles up the results of the peek operation into a token.
        *
        * @param {Int} start: The start position of the token
        * @param {Int} end: The end position of the token
        * @param {String} text: The actual text that was parsed
        * @param {TokenType} type: The TokenType of the token       
        */
        var t = function (start, end, text, type) {
            return {startPos: start, endPos: end, text: text, type: type};
        }

        /**
        * Peeks forward to see if the next token is the expected token and
        * returns the token if found.  Pos is not moved during a Peek operation.
        *
        * @param {TokenType} exepected: The types of token to scan for
        */
        var peek = function (expected) {
            var scanTokens = expected instanceof Array ? expected : [expected]
              , whiteSpace = /\s+/
              , token, curInput, m, scanToken, start, len
            
            scanTokens.push(whiteSpace);

            // loop past any skipped tokens and only look for expected tokens
            start = pos;
            while (!token || token.type === whiteSpace) {
                len = -1;
                curInput = input.substring(start);
                token = t(start, start, input.split(whiteSpace)[0]);
                
                var i, length = scanTokens.length;
                for(i = 0; i < length; i++) {
                    scanToken = scanTokens[i];
                    m = scanToken.exec(curInput);
                    if (m && m.index === 0 && m[0].length > len) {
                        len = m[0].length;
                        token = t(start, start + len, curInput.substring(0, len), scanToken);
                    }
                } 

                // update the start position if this token should be skipped
                if (token.type === whiteSpace) {
                    start = token.endPos;
                }
            }

            return token;
        }

        /**
        * Moves pos to the end of the expectedToken if it is found.
        *
        * @param {TokenType} exepectedToken: The types of token to scan for
        */
        var scan = function (expectedToken) {
            var token = peek(expectedToken);
            pos = token.endPos;
            return token;            
        }

        /**
        * Parses the next 'y-z' expression and returns the resulting valid
        * value array.
        *
        * @param {TokenType} tokenType: The type of range values allowed
        */
        var parseThroughExpr = function(tokenType) {

            var start = +parseTokenValue(tokenType)
              , end = checkAndParse(TOKENTYPES.through) ? 
                       +parseTokenValue(tokenType) : start
              , nums = [];

            for (var i = start; i <= end; i++) {
                nums.push(i);
            }
            
            return nums;
        }

        /**
        * Parses the next 'x,y-z' expression and returns the resulting valid
        * value array.
        *
        * @param {TokenType} tokenType: The type of range values allowed
        */
        var parseRanges = function(tokenType) {
            var nums = parseThroughExpr(tokenType);
            while (checkAndParse(TOKENTYPES.and)) {
                nums = nums.concat(parseThroughExpr(tokenType));
            }
            return nums;         
        }

        /**
        * Parses the next 'every (weekend|weekday|x) (starting on|between)' expression.
        *
        * @param {Recur} r: The recurrence to add the expression to
        */
        var parseEvery = function(r) {
            var num, period, start, end;

            if (checkAndParse(TOKENTYPES.weekend)) {
                r.on(NAMES.sun,NAMES.sat).dayOfWeek();
            }
            else if (checkAndParse(TOKENTYPES.weekday)) {
                r.on(NAMES.mon,NAMES.tue,NAMES.wed,NAMES.thu,NAMES.fri).dayOfWeek();
            }
            else {
                num = parseTokenValue(TOKENTYPES.rank);
                r.every(num);
                period = parseTimePeriod(r);

                if (checkAndParse(TOKENTYPES.start)) {
                    num = parseTokenValue(TOKENTYPES.rank);
                    r.startingOn(num);
                    parseToken(period.type);
                } 
                else if (checkAndParse(TOKENTYPES.between)) {
                    start = parseTokenValue(TOKENTYPES.rank);
                    if (checkAndParse(TOKENTYPES.and)) {
                        end = parseTokenValue(TOKENTYPES.rank);
                        r.between(start,end);
                    }
                }
            }            
        }

        /**
        * Parses the next 'on the (first|last|x,y-z)' expression.
        *
        * @param {Recur} r: The recurrence to add the expression to
        */
        var parseOnThe = function(r) {
            
            if (checkAndParse(TOKENTYPES.first)) {
                r.first();
            }
            else if (checkAndParse(TOKENTYPES.last)) {
                r.last();
            }
            else {
                r.on(parseRanges(TOKENTYPES.rank));
            }

            parseTimePeriod(r);
        }

        /**
        * Parses the schedule expression and returns the resulting schedules,
        * and exceptions.  Error will return the position in the string where
        * an error occurred, will be null if no errors were found in the
        * expression.
        *
        * @param {String} str: The schedule expression to parse
        */
        var parseScheduleExpr = function (str) {
            pos = 0;
            input = str;
            error = -1;

            var r = recur();
            while (pos < input.length && error < 0) {

                var token = parseToken([TOKENTYPES.every, TOKENTYPES.after, 
                    TOKENTYPES.onthe, TOKENTYPES.on, TOKENTYPES.of, TOKENTYPES['in'],
                    TOKENTYPES.at, TOKENTYPES.and, TOKENTYPES.except,
                    TOKENTYPES.also]);

                switch (token.type) {
                    case TOKENTYPES.every:
                        parseEvery(r);
                        break;
                    case TOKENTYPES.after:
                        r.after(parseTokenValue(TOKENTYPES.rank));
                        parseTimePeriod(r);
                        break;
                    case TOKENTYPES.onthe:
                        parseOnThe(r);
                        break;
                    case TOKENTYPES.on:
                        r.on(parseRanges(TOKENTYPES.dayName)).dayOfWeek();
                        break;
                    case TOKENTYPES.of:
                        r.on(parseRanges(TOKENTYPES.monthName)).month();
                        break;
                    case TOKENTYPES['in']:
                        r.on(parseRanges(TOKENTYPES.yearIndex)).year();
                        break;
                    case TOKENTYPES.at:
                        r.at(parseTokenValue(TOKENTYPES.time));
                        while (checkAndParse(TOKENTYPES.and)) {
                            r.at(parseTokenValue(TOKENTYPES.time));
                        }
                        break;
                    case TOKENTYPES.also:
                        r.and();
                        break;
                    case TOKENTYPES.except:
                        r.except();
                        break;
                    default:
                        error = pos;
                }
            }

            return {schedules: r.schedules, exceptions: r.exceptions, error: error};
        }

        /**
        * Parses the next token representing a time period and adds it to
        * the provided recur object.
        *
        * @param {Recur} r: The recurrence to add the time period to
        */
        var parseTimePeriod = function (r) {
            var timePeriod = parseToken([TOKENTYPES.second, TOKENTYPES.minute, 
                TOKENTYPES.hour, TOKENTYPES.dayOfYear, TOKENTYPES.dayOfWeek, 
                TOKENTYPES.dayInstance, TOKENTYPES.day, TOKENTYPES.month, 
                TOKENTYPES.year, TOKENTYPES.weekOfMonth, TOKENTYPES.weekOfYear]);

            switch (timePeriod.type) {
                case TOKENTYPES.second:
                    r.second();
                    break;         
                case TOKENTYPES.minute:
                    r.minute();
                    break;
                case TOKENTYPES.hour:
                    r.hour();
                    break;
                case TOKENTYPES.dayOfYear:
                    r.dayOfYear();
                    break;             
                case TOKENTYPES.dayOfWeek:
                    r.dayOfWeek();
                    break;
                case TOKENTYPES.dayInstance:
                    r.dayOfWeekCount();
                    break;
                case TOKENTYPES.day:
                    r.dayOfMonth();
                    break;
                case TOKENTYPES.weekOfMonth:
                    r.weekOfMonth();
                    break;
                case TOKENTYPES.weekOfYear:
                    r.weekOfYear();
                    break;
                case TOKENTYPES.month:
                    r.month();
                    break;
                case TOKENTYPES.year:
                    r.year();
                    break;
                default:
                    error = pos;
            }

            return timePeriod;
        }

        /**
        * Checks the next token to see if it is of tokenType. Returns true if
        * it is and discards the token.  Returns false otherwise.
        *
        * @param {TokenType} tokenType: The type or types of token to parse
        */
        var checkAndParse = function (tokenType) {
            var found = (peek(tokenType)).type === tokenType;
            if (found) {
                scan(tokenType);
            }
            return found;
        }

        /**
        * Parses and returns the next token.
        *
        * @param {TokenType} tokenType: The type or types of token to parse
        */
        var parseToken = function (tokenType) {
            var t = scan(tokenType);
            if (t.type) {
                t.text = convertString(t.text, tokenType)
            }
            else {
                error = pos;
            }
            return t;
        }

        /**
        * Returns the text value of the token that was parsed.
        *
        * @param {TokenType} tokenType: The type of token to parse
        */
        var parseTokenValue = function (tokenType) {
            return (parseToken(tokenType)).text;
        }

        /**
        * Converts a string value to a numerical value based on the type of
        * token that was parsed.
        *
        * @param {String} str: The schedule string to parse
        * @param {TokenType} tokenType: The type of token to convert
        */
        var convertString = function (str, tokenType) {
            var output = str;

            switch (tokenType) {
                case TOKENTYPES.time:
                    var parts = str.split(/(:|am|pm)/)
                      , hour = parts[3] === 'pm' ? parseInt(parts[0],10) + 12 : parts[0]
                      , min = parts[2].trim();

                    output = (hour.length === 1 ? '0' : '') + hour + ":" + min;
                    break;

                case TOKENTYPES.rank:
                    output = parseInt((/^\d+/.exec(str))[0],10);
                    break;

                case TOKENTYPES.monthName:
                case TOKENTYPES.dayName:
                    output = NAMES[str.substring(0,3)];
                    break;
            }

            return output;
        }

        return {

            /**
            * Parses a schedule string.  Returns the schedule, exceptions, and
            * an error position if an error was hit.
            *
            * @param {String} str: The schedule string to parse
            * @api public
            */
            parse: function(str) {
                return parseScheduleExpr(str.toLowerCase());
            }
        };
    };

    /**
    * Allow library to be used within both the browser and node.js
    */
/*    if (typeof exports !== 'undefined') {
        module.exports = EnParser;
    } else {
        window.enParser = EnParser;
    } */

    var root = (typeof exports !== "undefined" && exports !== null ? exports : window);
    root.enParser = EnParser;

}).call(this,this);


/**
* Later.js 0.0.1
* (c) 2012 Bill, BunKat LLC.
* Later is freely distributable under the MIT license.
* For all details and documentation:
*     http://bunkat.github.com/later
*/
(function(window) {

    "use strict";

    /**
    * Calculates the next occurrence (or occcurrences) of a given schedule.
    * Schedules are simply a set of constraints that must be met for a 
    * particular date to be valid. Schedules can be generated using Recur or
    * can be created directly.  
    *
    * Schedules have the following form:
    *
    * {
    *   schedules: [
    *       {
    *           constraintId: [valid values],
    *           constraintId: [valid values],
    *           ...
    *       },
    *       {
    *           constraintId: [valid values],
    *           constraintId: [valid values],
    *           ...
    *       }
    *       ...
    *   ],
    *   exceptions: [
    *       {
    *           constraintId: [valid values],
    *           constraintId: [valid values],
    *           ...         
    *       },
    *       {
    *           constraintId: [valid values],
    *           constraintId: [valid values],
    *           ...         
    *       },
    *       ...
    *   ]   
    * }
    *
    * See Recur.js for the available constraints and their value ranges.  May
    * also be useful to create a schedule using Recur and then examining the
    * schedule that is produced.
    */

    /**
    * Initializes the Later object. 
    *
    * @param {Int} resolution: Minimum number of seconds between occurrences
    * @param {Bool} useLocalTime: True if local time zone should be used
    * @api public
    */
    var Later = function(resolution, useLocalTime) {

        var isLocal = useLocalTime || false
          , get = 'get' + (isLocal ? '' : 'UTC')
          , exec = true
          
          // constants
          , SEC = 1000
          , MIN = SEC * 60
          , HOUR = MIN * 60
          , DAY = HOUR * 24

          // aliases for common math functions
          , ceil = Math.ceil        
          , floor = Math.floor
          , max = Math.max

          // data prototypes to switch between UTC and local time calculations
          , dateProto = Date.prototype
          , getYear = dateProto[get + 'FullYear']
          , getMonth = dateProto[get + 'Month']
          , getDate = dateProto[get + 'Date']
          , getDay = dateProto[get + 'Day']
          , getHour = dateProto[get + 'Hours']
          , getMin = dateProto[get + 'Minutes']
          , getSec = dateProto[get + 'Seconds'];

          // minimum time between valid occurrences in seconds
          if (resolution == null) resolution = 1;

        /**
        * Finds the next valid value which is either the next largest valid
        * value or the minimum valid value if no larger value exists. To
        * simplify some calculations, the min value is then added to a specified
        * offset.  
        * 
        * For example, if the current minute is 5 and the next valid 
        * value is 1, the offset will be set to 60 (max number of minutes) and
        * nextInRange will return 61. This is the number of minutes that must
        * be added to the current hour to get to the next valid minute.
        *
        * @param {Int/String} val: The current value
        * @param {[]} values: Array of possible valid values
        * @param {Int/String} minOffset: Value to add to the minimum value
        */
        var nextInRange = function(val, values, minOffset) {
            var cur, next, min = values[0], i = values.length;
            while (i--) {
                cur = values[i];
                if (cur === val) {
                    return val;
                }
                min = cur < min ? cur : min;
                next = cur > val && (!next || cur < next) ? cur : next;             
            }

            return next || (min + minOffset);
        };

        /**
        * Builds and returns a new Date using the specified values.  Date
        * returned is either using Local time or UTC based on isLocal. 
        *
        * @param {Int} yr: Four digit year
        * @param {Int} mt: Month between 0 and 11
        * @param {Int} dt: Date between 1 and 31
        * @param {Int} hr: Hour between 0 and 23, defaults to 0
        * @param {Int} mn: Minute between 0 and 59, defaults to 0
        * @param {Int} sc: Second between 0 and 59, defaults to 0
        */
        var date = function(yr, mt, dt, hr, mn, sc) {
            return isLocal ? new Date(yr, mt, dt, hr || 0, mn || 0, sc || 0) :
                new Date(Date.UTC(yr, mt, dt, hr || 0, mn || 0, sc || 0));
        };

        /**
        * Pads a digit with a leading zero if it is less than 10.
        *
        * @param {Int} val: The value that needs to be padded
        */
        var pad = function(val) {
            return (val < 10 ? '0' : '') + val;
        };

        /**
        * Calculates the next valid occurrence of a particular schedule that 
        * occurs on or after the specified start time. 
        *
        * @param {object} schedule: Valid schedule object containing constraints
        * @param {Date} start: The first possible valid occurrence
        */
        var getNextForSchedule = function(sched, start, end) {
            var next, inc, x, cur
              , Y, M, D, d, h, m, s
              , oJan1, oMonthStart, oWeekStart, oWeekStartY, oMonthEnd
              , oDec31
              , t, dy, wy, wm, dc
              , daysInYear, daysInMonth, firstDayOfMonth
              , weekStart, weeksInYear, weeksInMonth
              , maxLoopCount = 1000;

            // handle any after constraints
            next = after(start, sched);

            // It's not pretty, but just keep looping through all of the
            // constraints until they have all been met (or no valid 
            // occurrence exists). All calculations are done just in time and 
            // and only once to prevent extra work from being done each loop.
            while (next && maxLoopCount--) {

                // make sure we are still with in the boundaries
                if (end && next.getTime() > end.getTime()) {
                    return null;
                }

                // check year
                Y = getYear.call(next);
                if (sched.Y && (inc = nextInRange(Y, sched.Y, 0)) !== Y ) {
                    next = inc > Y ? date(inc,0,1) : null;
                    continue;
                }


                // check day of year (one based)
                oJan1 = date(Y, 0, 1);
                oDec31 = date(Y + 1, 0, 0);
                if (sched.dy) {
                    dy = ceil((next.getTime() - oJan1.getTime() + 1)/DAY);
                    daysInYear = ceil((oDec31.getTime() - oJan1.getTime() + 1)/DAY);                    
                    if ((inc = nextInRange(dy, sched.dy, daysInYear)) !== dy) {
                        next = date(Y, 0, inc);
                        continue;
                    } 
                }

                // check month (one based)
                M = getMonth.call(next);
                if (sched.M && (inc = nextInRange(M+1, sched.M, 12)) !== M+1) {
                    next = date(Y, inc-1, 1);
                    continue;
                }

                // check week of year (one based, ISO week)
                D = getDate.call(next);
                d = getDay.call(next);
                if (sched.wy) {
                    oWeekStart = date(Y, M, D + 4 - (d || 7));      
                    oWeekStartY = date(getYear.call(oWeekStart),0,1);           
                    weeksInYear = getDay.call(oJan1) === 4 || 
                        getDay.call(oDec31) === 4 ? 53 : 52;
                    
                    wy = ceil((((oWeekStart.getTime()-oWeekStartY.getTime())/DAY)+1)/7);
                    if ((inc = nextInRange(wy, sched.wy, weeksInYear)) !== wy) {
                        next = date(
                                getYear.call(oWeekStart),
                                getMonth.call(oWeekStart),
                                getDate.call(oWeekStart) - 3 + (inc - wy) * 7);
                        continue;
                    }
                }

                // check date of month (one based)
                oMonthEnd = date(Y, M + 1, 0);
                daysInMonth = getDate.call(oMonthEnd);
                if (sched.D && (inc = nextInRange(D, sched.D, daysInMonth)) !== D) {
                    next = date(Y, M, inc);
                    continue;
                }

                // check week of month (one based, 0 for last week of month)
                if (sched.wm) {
                    firstDayOfMonth = getDay.call(date(Y, M, 1));
                    wm = floor((((D + firstDayOfMonth - 1)/7))+1);
                    weeksInMonth = floor((((daysInMonth + firstDayOfMonth - 1)/7))+1);
                    if ((inc = nextInRange(wm, sched.wm, weeksInMonth)) !== wm) {
                        // jump to the Sunday of the desired week, making sure not
                        // to double count the last week in the month if we cross
                        // a month boundary, set to 1st of month for week 1
                        next = date(Y, M, 
                            (inc-1) * 7 - (firstDayOfMonth - 1)                                             
                            - (inc > weeksInMonth && getDay.call(oMonthEnd) < 6 ? 7 : 0)
                            + (inc === weeksInMonth + 1 ? getDay.call(oMonthEnd) + 1 : 0));
                        continue;
                    }
                }

                // check day of week (zero based)
                if (sched.d && (inc = nextInRange(d+1, sched.d, 7)) !== d+1) {
                    next = date(Y, M, D + (inc-1) - d);
                    continue;
                }

                // check day of week count (one based, 0 for last instance)
                if (sched.dc) {
                    dc = floor((D - 1) / 7) + 1;
                    if ((inc = nextInRange(dc, sched.dc, 0)) !== dc) {
                        if (inc > 0) {
                            next = date(Y, M + (inc < dc ? 1 : 0), 1 + (7 * (inc-1)));
                            continue;
                        }
                        //special last day instance of month constraint
                        if (inc < 1 && D < (daysInMonth - 6)) {
                            next = date(Y, M, daysInMonth - 6);
                            continue;                       
                        }
                    }               
                }

                // check hour of day (zero based)
                h = getHour.call(next);
                if (sched.h && (inc = nextInRange(h, sched.h, 24)) !== h) {
                    next = date(Y, M, D, inc);
                    continue;
                }           

                // check minute of hour (zero based)
                m = getMin.call(next);      
                if (sched.m && (inc = nextInRange(m, sched.m, 60)) !== m) {
                    next = date(Y, M, D, h, inc);
                    continue;
                }

                // check second of minute (zero based)
                s = getSec.call(next);          
                if (sched.s && (inc = nextInRange(s, sched.s, 60)) !== s) {
                    next.setSeconds(inc);
                    next = date(Y, M, D, h, m, inc);
                    continue;
                }

                // check time of day (24-hr)
                if (sched.t) {
                    t = pad(h) +':'+ pad(m) +':'+ pad(s);
                    if ((inc = nextInRange(t, sched.t, '')) !== t) {
                        x = inc.split(':');
                        next = date(Y, M, D + (t > inc ? 1 : 0), x[0], x[1], x[2]);
                        continue;
                    }
                }

                // if we make it this far, all constraints have been met
                break;
            }

            return maxLoopCount > 0 ? next : null;
        };

        /**
        * Increments a date by a given amount of time.  Date
        * returned is either using Local time or UTC based on isLocal. 
        *
        * @param {Int} yr: Number of years to increment by
        * @param {Int} mt: Number of months to increment by
        * @param {Int} dt: Number of days to increment by
        * @param {Int} hr: Number of hours to increment by
        * @param {Int} mn: Number of minutes to increment by
        * @param {Int} sc: Number of seconds to increment by
        */
        var after = function (start, sched) {
            var yr = getYear.call(start) + getAfter(sched.aY)
              , mt = getMonth.call(start) + getAfter(sched.aM)
              , dt = getDate.call(start) + 
                    max(getAfter(sched.aD), getAfter(sched.ady), getAfter(sched.ad),
                        getAfter(sched.awy) * 7, getAfter(sched.awm) * 7)
              , hr = getHour.call(start) + getAfter(sched.ah)
              , mn = getMin.call(start) + getAfter(sched.am)
              , sc = getSec.call(start) + getAfter(sched.as);

            return date(yr, mt, dt, hr, mn, sc);
        }

        /**
        * Returns the value of an after constraint or 0 if not set. 
        *
        * @param {Array} constraint: After constrant to check
        */
        var getAfter = function (constraint) {
            return constraint && constraint[0] ? constraint[0] : 0;
        }

        /**
        * Returns a new date object that represents the next possible valid
        * occurrence based on the resolution that has beeen configured.
        *
        * @param {Date} date: The Date object to be incremented
        */
        var tick = function (date) {
            return new Date(date.getTime() + (resolution * 1000));
        };

        return {

            /**
            * Returns true if the specified date meets all of the constraints
            * defined within the specified schedule. 
            *
            * @param {Recur} recur: Set of schedule and exception constraints
            * @param {Date} date: The date to validate against
            * @api public
            */
            isValid: function (recur, date) {
                return date.getTime() === this.getNext(recur, date).getTime();
            },

            /**
            * Returns the next one or more valid occurrences of a schedule. 
            *
            * @param {Recur} recur: Set of schedule and exception constraints
            * @param {Int} count: The number of occurrences to return
            * @param {Date} startDate: The initial date to start looking from
            * @param {Date} endDate: The last date to include
            * @api public
            */
            get: function (recur, count, startDate, endDate) {
                var occurrences = []                
                  , date;
                 
                while (count-- > 0 && (date = 
                        this.getNext(recur, date || startDate, endDate))) {
                    occurrences.push(date);
                    date = tick(date);
                }

                return occurrences;
            },

            /**
            * Returns the next valid occurrence of a schedule. 
            *
            * @param {Recur} recur: Set of schedule and exception constraints
            * @param {Date} startDate: The initial date to start looking from
            * @param {Date} endDate: The last date to include
            * @api public
            */
            getNext: function (recur, startDate, endDate) {
                var schedules = recur.schedules || []
                  , exceptions = {schedules: recur.exceptions || []}
                  , start = startDate || new Date()               
                  , date, tDate
                  , i = schedules.length;
                
                // return null if we're past the specified end date
                if (endDate && startDate.getTime() > endDate.getTime()) {
                    return null;
                }
                                    
                while(i--) {
                    tDate = getNextForSchedule(schedules[i], start, endDate);
                    if (!date || (tDate < date)) {
                        date = tDate;
                    }
                }

                if (date && exceptions.schedules.length > 0 &&
                        this.isValid (exceptions, date)) {
                    date = this.getNext(recur, tick(date));
                }

                return date;                
            },

            /**
            * Executes the provided callback on the provided recurrence 
            * schedule. Returns true if the timer was started.
            *
            * @param {Recur} recur: Set of schedule and exception constraints
            * @param {Date} startDate: The initial date to start looking from
            * @param {Func} callback: The function to execute
            * @param {arg[]} arg: Argument or array of arguments to pass to the
            *                     callback
            * @api public
            */
            exec: function (recur, startDate, callback, arg) {
                var next = this.getNext(recur, tick(startDate));

                if (next) {
                    next = next.getTime() - (new Date()).getTime();
                    exec = setTimeout(this.handleExec, next, this, recur, callback, arg);
                    return true;
                }
            },

            /**
            * Immediately stops the execution of the current timer if one
            * exists.
            *
            * @api public
            */
            stopExec: function () {
                if (exec) {
                    clearTimeout(exec);
                    exec = false;
                }
            },

            /**
            * Handles the execution of the timer.
            *
            * @param {Later} later: The context to execute within
            * @param {Recur} recur: The set of constraints to use
            * @param {Func} callback: The function to execute
            * @param {arg[]} arg: Argument or array of arguments to pass to the
            *                     callback
            */
            handleExec: function (later, recur, callback, arg) {
                callback(arg);
                if (exec) {
                    later.exec(recur, (new Date()), callback, arg);
                }
            }

        };
    };

    /**
    * Allow library to be used within both the browser and node.js
    */
    var root = typeof exports !== "undefined" && exports !== null ? exports : window;
    root.later = Later;

}).call(this,this);



/**
* Later.js 0.0.1
* (c) 2012 Bill, BunKat LLC.
* Later is freely distributable under the MIT license.
* For all details and documentation:
*     http://bunkat.github.com/later
*/
(function (window) {

    "use strict";
    
    /**
    * Simple API for generating valid schedules for Later.js.  All commands
    * are chainable.
    * 
    * Example:
    *
    * Every 5 minutes between minutes 15 and 45 of each hour and also 
    * at 9:00 am every day, except in the months of January and February
    *
    * recur().every(5).minute().between(15, 45).and().at('09:00:00')
    *        .except().on(0, 1).month();
    */
    var Recur = function () {
    
        var schedules = [{}]
          , exceptions = []
          , cur = schedules[0]
          , curArr = schedules
          , curName
          , values, every, after, applyMin, applyMax, i
          , last;

        /**
        * Adds values to the specified constraint in the current schedule.
        *
        * @param {String} name: Name of constraint to add
        * @param {Int} min: Minimum value for this constraint
        * @param {Int} max: Maximum value for this constraint
        */
        var add = function (name, min, max) {
            name = after ? 'a' + name : name;

            if (!cur[name]) {
                cur[name] = [];
            }

            curName = cur[name];

            if (every) {                    
                values = [];
                for (i = min; i <= max; i += every) {
                    values.push(i);
                }

                // save off values in case of startingOn or between
                last = {n: name, x: every, c: curName.length, m: max};
            }

            values = applyMin ? [min] : applyMax ? [max] : values;
            var length = values.length;
            for (i = 0; i < length; i += 1) {
                if (curName.indexOf(values[i]) < 0) {
                    curName.push(values[i]);
                }   
            }

            // reset the built up state
            values = every = after = applyMin = applyMax = 0;       
        };

        return {

            /**
            * Set of constraints that must be met for an occurrence to be valid.
            *
            * @api public
            */          
            schedules: schedules,

            /**
            * Set of exceptions that must not be met for an occurrence to be 
            * valid.
            *
            * @api public
            */
            exceptions: exceptions,

            /**
            * Specifies the specific instances of a time period that are valid. 
            * Must be followed by the desired time period (minute(), hour(), 
            * etc). For example, to specify a schedule for the 5th and 25th 
            * minute of every hour:
            *
            * recur().on(5, 25).minute();
            *
            * @param {Int} args: One or more valid instances
            * @api public
            */
            on: function () { 
                values = arguments[0] instanceof Array ? arguments[0] : arguments;
                return this; 
            },

            /**
            * Specifies the recurring interval of a time period that are valid. 
            * Must be followed by the desired time period (minute(), hour(), 
            * etc). For example, to specify a schedule for every 4 hours in the 
            * day:
            *
            * recur().every(4).hour();
            *
            * @param {Int} x: Recurring interval
            * @api public
            */
            every: function (x) {
                every = x;
                return this;
            },

            /**
            * Specifies the minimum interval between occurrences. 
            * Must be followed by the desired time period (minute(), hour(), 
            * etc). For example, to specify a schedule that occurs after four hours
            * from the start time:
            *
            * recur().after(4).hour();
            *
            * @param {Int} x: Recurring interval
            * @api public
            */
            after: function (x) {
                after = true;
                values = [x];
                return this;
            },

            /**
            * Specifies that the first instance of a time period is valid. Must
            * be followed by the desired time period (minute(), hour(), etc). 
            * For example, to specify a schedule for the first day of every 
            * month:
            *
            * recur().first().dayOfMonth();
            *
            * @api public
            */
            first: function () {
                applyMin = 1;
                return this;
            },

            /**
            * Specifies that the last instance of a time period is valid. Must
            * be followed by the desired time period (minute(), hour(), etc). 
            * For example, to specify a schedule for the last day of every year:
            *
            * recur().last().dayOfYear();
            *
            * @api public
            */
            last: function () {
                applyMax = 1;
                return this;
            },

            /**
            * Specifies a specific time that is valid. Time must be specified in
            * hh:mm:ss format using 24 hour time. For example, to specify 
            * a schedule for 8:30 pm every day:
            *
            * recur().at('20:30:00');
            *
            * @param {String} time: Time in hh:mm:ss 24-hour format
            * @api public
            */
            at: function () {
                values = arguments;
                var i, length = values.length;
                for (var i = 0; i < length; i++) {
                    var split = values[i].split(':');
                    if (split.length < 3) {
                        values[i] += ':00';
                    }
                }

                add('t');
                return this;
            },

            /**
            * Seconds time period, denotes seconds within each minute.
            * Minimum value is 0, maximum value is 59. Specify 59 for last.  
            *
            * recur().on(5, 15, 25).second();
            *
            * @api public
            */
            second: function () {
                add('s', 0, 59);
                return this;
            },

            /**
            * Minutes time period, denotes minutes within each hour.
            * Minimum value is 0, maximum value is 59. Specify 59 for last.  
            *
            * recur().on(5, 15, 25).minute();
            *
            * @api public
            */
            minute: function () {
                add('m', 0, 59);
                return this;
            },

            /**
            * Hours time period, denotes hours within each day.
            * Minimum value is 0, maximum value is 23. Specify 23 for last.  
            *
            * recur().on(5, 15, 25).hour();
            *
            * @api public
            */
            hour: function () {
                add('h', 0, 23);
                return this;
            },

            /**
            * Days of month time period, denotes number of days within a month.
            * Minimum value is 1, maximum value is 31.  Specify 0 for last.  
            *
            * recur().every(2).dayOfMonth();
            *
            * @api public
            */
            dayOfMonth: function () {
                add('D', 1, applyMax ? 0 : 31);
                return this;
            },

            /**
            * Days of week time period, denotes the days within a week.
            * Minimum value is 1, maximum value is 7.  Specify 0 for last.
            * 1 - Sunday
            * 2 - Monday
            * 3 - Tuesday
            * 4 - Wednesday
            * 5 - Thursday
            * 6 - Friday
            * 7 - Saturday  
            *
            * recur().on(1).dayOfWeek();
            *
            * @api public
            */
            dayOfWeek: function () {
                add('d', 1, 7);
                return this;
            },

            /**
            * Short hand for on(1,7).dayOfWeek()
            *
            * @api public
            */
            onWeekend: function() {
                values = [1,7];
                return this.dayOfWeek();
            },

            /**
            * Short hand for on(2,3,4,5,6).dayOfWeek()
            *
            * @api public
            */
            onWeekday: function() {
                values = [2,3,4,5,6];
                return this.dayOfWeek();
            },

            /**
            * Days of week count time period, denotes the number of times a 
            * particular day has occurred within a month.  Used to specify
            * things like second Tuesday, or third Friday in a month.
            * Minimum value is 1, maximum value is 5.  Specify 0 for last.
            * 1 - First occurrence
            * 2 - Second occurrence
            * 3 - Third occurrence
            * 4 - Fourth occurrence
            * 5 - Fifth occurrence
            * 0 - Last occurrence  
            *
            * recur().on(1).dayOfWeek().on(1).dayOfWeekCount();
            *
            * @api public
            */
            dayOfWeekCount: function () {
                add('dc', 1, applyMax ? 0 : 5);
                return this;
            },

            /**
            * Days of year time period, denotes number of days within a year.
            * Minimum value is 1, maximum value is 366.  Specify 0 for last.  
            *
            * recur().every(2).dayOfYear();
            *
            * @api public
            */
            dayOfYear: function () {
                add('dy', 1, applyMax ? 0 : 366);
                return this;
            },

            /**
            * Weeks of month time period, denotes number of weeks within a 
            * month. The first week is the week that includes the 1st of the
            * month. Subsequent weeks start on Sunday.    
            * Minimum value is 1, maximum value is 5.  Specify 0 for last.  
            * February 2nd,  2012 - Week 1
            * February 5th,  2012 - Week 2
            * February 12th, 2012 - Week 3
            * February 19th, 2012 - Week 4
            * February 26th, 2012 - Week 5 (or 0)
            *
            * recur().on(2).weekOfMonth();
            *
            * @api public
            */
            weekOfMonth: function () {
                add('wm', 1, applyMax ? 0 : 5);
                return this;
            },

            /**
            * Weeks of year time period, denotes the ISO 8601 week date. For 
            * more information see: http://en.wikipedia.org/wiki/ISO_week_date.
            * Minimum value is 1, maximum value is 53.  Specify 0 for last.
            *
            * recur().every(2).weekOfYear();
            *
            * @api public
            */
            weekOfYear: function () {
                add('wy', 1, applyMax ? 0 : 53);
                return this;
            },

            /**
            * Month time period, denotes the months within a year.
            * Minimum value is 1, maximum value is 12.  Specify 0 for last.
            * 1 - January
            * 2 - February
            * 3 - March
            * 4 - April
            * 5 - May
            * 6 - June
            * 7 - July
            * 8 - August
            * 9 - September
            * 10 - October
            * 11 - November
            * 12 - December  
            *
            * recur().on(1).dayOfWeek();
            *
            * @api public
            */
            month: function () {
                add('M', 1, 12);
                return this;
            },

            /**
            * Year time period, denotes the four digit year.
            * Minimum value is 1970, maximum value is 2450 (arbitrary)  
            *
            * recur().on(2011, 2012, 2013).year();
            *
            * @api public
            */
            year: function () {
                add('Y', 1970, 2450);
                return this;
            },

            /**
            * Modifies a recurring interval (specified using every) to start
            * at a given offset.  To create a schedule for every 5 minutes
            * starting on the 6th minute - making minutes 6, 11, 16, etc valid:
            *
            * recur().every(5).minute().startingOn(6);
            *
            * @param {Int} start: The desired starting offset
            * @api public
            */
            startingOn: function (start) {
                return this.between(start, last.m);
            },

            /**
            * Modifies a recurring interval (specified using every) to start
            * and stop at specified times.  To create a schedule for every 
            * 5 minutes starting on the 6th minute and ending on the 11th 
            * minute - making minutes 6 and 11 valid:
            *
            * recur().every(5).minute().between(6, 11);
            *
            * @param {Int} start: The desired starting offset
            * @param {Int} end: The last valid value
            * @api public
            */
            between: function (start, end) {
                // remove the values added as part of specifying the last
                // time period and replace them with the new restricted values
                cur[last.n] = cur[last.n].splice(0, last.c);
                every = last.x;
                add(last.n, start, end);
                return this;
            },

            /**
            * Creates a composite schedule.  With a composite schedule, a valid
            * occurrence of any of the component schedules is considered a valid
            * value for the composite schedule (e.g. they are OR'ed together).
            * To create a schedule for every 5 minutes on Mondays and every 10
            * minutes on Tuesdays:
            *
            * recur().every(5).minutes().on(1).dayOfWeek().and().every(10)
            *        .minutes().on(2).dayOfWeek();
            *
            * @api public
            */
            and: function () {
                cur = curArr[curArr.push({}) - 1];
                return this;
            },

            /**
            * Creates exceptions to a schedule. Any valid occurrence of the 
            * exception schedule (which may also be composite schedules) is
            * considered a invalid schedule occurrence. Everything that follows
            * except will be treated as an exception schedule.  To create a 
            * schedule for 8:00 am every Tuesday except for patch Tuesday 
            * (second Tuesday each month):
            *
            * recur().at('08:00:00').on(2).dayOfWeek().except()
            *        .dayOfWeekCount(1);
            *
            * @api public
            */
            except: function () {
                exceptions.push({});
                curArr = exceptions;
                cur = exceptions[0];
                return this;
            }
        };
    };

    /**
    * Allow library to be used within both the browser and node.js
    */
    var root = typeof exports !== "undefined" && exports !== null ? exports : window;
    root.recur = Recur;

}).call(this,this);


