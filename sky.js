// Translations
const translations = {
  newMoon: 'ירח חדש',
  waxingCrescent: 'ירח בתהליך התמלאות ראשוני',
  firstQuarter: 'רבע ראשון',
  waxingGibbous: 'ירח גבנוני בתהליך התמלאות',
  fullMoon: 'ירח מלא',
  waningGibbous: 'ירח גבנוני בתהליך התמעטות',
  lastQuarter: 'רבע אחרון',
  waningCrescent: 'ירח בתהליך התמעטות סופי',
  winter: 'חורף',
  spring: 'אביב',
  summer: 'קיץ',
  fall: 'סתיו',
  day: 'יום',
  daysOf: 'מתוך',
  daysRemaining: 'ימים נותרו',
  dayHour: 'שעת יום',
  nightHour: 'שעת לילה',
  minutes: 'דקות',
  title: 'מעקב יום ירחי ועונה',
  currentDate: 'תאריך נוכחי:',
  currentTime: 'שעה נוכחית:',
  location: 'מיקום:',
  coordinates: 'קואורדינטות:',
  lunarDay: 'יום ירחי',
  ofLunarCycle: 'מתוך מחזור ירחי של 29.5 ימים',
  seasonTitle: 'עונה',
  seasonalHours: 'שעות עונתיות',
  sunriseTime: 'זריחה:',
  sunsetTime: 'שקיעה:',
  dayLength: 'משך היום:',
  nightLength: 'משך הלילה:',
  currentHour: 'שעה נוכחית:',
  hourProgress: 'התקדמות השעה:',
  locationName: 'ירושלים, ישראל',
  note: 'הערה: תאריכי העונות הם משוערים ומבוססים על חצי הכדור הצפוני.',
  locating: 'מאתר מיקום...',
  locationFound: 'מיקום נמצא!',
  permissionDenied: 'המשתמש סירב לבקשת המיקום.',
  positionUnavailable: 'מידע המיקום אינו זמין.',
  requestTimeout: 'פג תוקף הבקשה לאיתור המיקום.',
  notSupported: 'שגיאה: Geolocation אינו נתמך בדפדפן שלך.'
};

// Get latitude and longitude from URL parameters if provided
function getCoordinatesFromURL() {
  const urlParams = new URLSearchParams(window.location.search);
  const lat = parseFloat(urlParams.get('lat'));
  const lon = parseFloat(urlParams.get('lon'));
  
  // Default coordinates for Jerusalem
  const defaultCoordinates = {
    latitude: 31.7781,
    longitude: 35.2360
  };
  
  // Check if valid coordinates were provided
  if (!isNaN(lat) && !isNaN(lon) && 
      lat >= -90 && lat <= 90 && 
      lon >= -180 && lon <= 180) {
    return {
      latitude: lat,
      longitude: lon
    };
  }
  
  return defaultCoordinates;
}

// Initialize coordinates
let coordinates = getCoordinatesFromURL();

// Display custom location if using non-default coordinates
function displayLocationInfo() {
  const defaultCoordinates = {
    latitude: 31.7781,
    longitude: 35.2360
  };
  
  const locationElement = document.getElementById('locationDisplay');
  
  // If using custom coordinates, show the location info
  if (coordinates.latitude !== defaultCoordinates.latitude || 
      coordinates.longitude !== defaultCoordinates.longitude) {
    locationElement.textContent = `${translations.coordinates} ${coordinates.latitude.toFixed(4)}, ${coordinates.longitude.toFixed(4)}`;
    locationElement.hidden = false;
  } else {
    locationElement.hidden = true;
  }
}

// Global variables to store current state
let currentSunTimes = null;
let currentSeasonalHour = null;

// Set up dark mode toggle
const body = document.body;
const themeToggle = document.getElementById('themeToggle');

function toggleDarkMode() {
  body.classList.toggle('dark-mode');
  body.classList.toggle('light-mode');
  
  // Update theme toggle icon
  if (body.classList.contains('dark-mode')) {
    themeToggle.innerHTML = `
      <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <circle cx="12" cy="12" r="5"></circle>
        <path d="M12 1v2"></path>
        <path d="M12 21v2"></path>
        <path d="M4.22 4.22l1.42 1.42"></path>
        <path d="M18.36 18.36l1.42 1.42"></path>
        <path d="M1 12h2"></path>
        <path d="M21 12h2"></path>
        <path d="M4.22 19.78l1.42-1.42"></path>
        <path d="M18.36 5.64l1.42-1.42"></path>
      </svg>
    `;
  } else {
    themeToggle.innerHTML = `
      <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"></path>
      </svg>
    `;
  }
  
  // Update all visual elements
  updateAllVisuals();
}

themeToggle.addEventListener('click', toggleDarkMode);

// Check system preference for dark mode
if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
  toggleDarkMode();
}

function updateDateTime() {
  const now = new Date();
  const dateElement = document.getElementById('currentDate');
  const timeElement = document.getElementById('currentTime');
  const weekContainer = document.getElementById('weekVisualization');

  const dateOptions = {
      weekday: 'long'
    };
  
  // Update time
  const timeOptions = {
    hour: '2-digit',
    minute: '2-digit'
  };
  
  dateElement.textContent = new Intl.DateTimeFormat('he-IL', dateOptions).format(now);
  timeElement.textContent = new Intl.DateTimeFormat('he-IL', timeOptions).format(now);

  // Calculate day of week (0 = Sunday, 6 = Saturday)
  const dayOfWeek = now.getDay(); // 0 (Sunday) to 6 (Saturday)

  // Clear previous visualization
  weekContainer.innerHTML = '';

  // Create SVG using D3
  const svg = d3.select('#weekVisualization')
    .append('svg')
    .attr('class', 'week-circle-svg')
    .attr('viewBox', '0 0 100 100');

  const radius = 40;
  const centerX = 50;
  const centerY = 50;
  const segmentAngle = (2 * Math.PI) / 7; // 360 degrees / 7 days

  // Generate 7 segments
  for (let i = 0; i < 7; i++) {
    const startAngle = i * segmentAngle - Math.PI / 2; // Start from top (Sunday)
    const endAngle = (i + 1) * segmentAngle - Math.PI / 2;

    const x1 = centerX + radius * Math.cos(startAngle);
    const y1 = centerY + radius * Math.sin(startAngle);
    const x2 = centerX + radius * Math.cos(endAngle);
    const y2 = centerY + radius * Math.sin(endAngle);

    const largeArcFlag = segmentAngle > Math.PI ? 1 : 0;

    const pathD = [
      `M ${centerX},${centerY}`, // Move to center
      `L ${x1},${y1}`,           // Line to start
      `A ${radius},${radius} 0 ${largeArcFlag} 1 ${x2},${y2}`, // Arc to end
      'Z'                        // Close path
    ].join(' ');

    svg.append('path')
      .attr('d', pathD)
      .attr('class', 'week-segment' + (i <= dayOfWeek ? ' filled' : ''))
      .attr('data-day', i); // Optional: for debugging or future use
  }
}
  
// Calculate lunar day (1-29.5)
function calculateLunarDay(date) {
  // Lunar cycle is approximately 29.53 days
  // New moon reference date (known new moon)
  const refNewMoon = new Date('2025-03-01T08:24:00Z');
  const lunarCycle = 29.53; // days
  
  // Calculate days since reference new moon
  const msDiff = date.getTime() - refNewMoon.getTime();
  const daysDiff = msDiff / (1000 * 60 * 60 * 24);
  
  // Calculate current lunar day (1-29.53)
  const currentLunarDay = (daysDiff % lunarCycle) + 1;
  
  return currentLunarDay;
}

// Determine lunar phase based on lunar day
function getLunarPhase(lunarDay) {
  if (lunarDay < 1.5) return translations.newMoon;
  if (lunarDay < 7) return translations.waxingCrescent;
  if (lunarDay < 8.5) return translations.firstQuarter;
  if (lunarDay < 14) return translations.waxingGibbous;
  if (lunarDay < 16) return translations.fullMoon;
  if (lunarDay < 22) return translations.waningGibbous;
  if (lunarDay < 23.5) return translations.lastQuarter;
  return translations.waningCrescent;
}

// Get season icon based on season name
function getSeasonIcon(season) {
  let iconSvg = '';
  
  switch(season) {
    case translations.spring:
      iconSvg = `
        <svg class="svg-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M12 2v2"></path>
          <path d="M12 20v2"></path>
          <path d="M4.93 4.93l1.41 1.41"></path>
          <path d="M17.66 17.66l1.41 1.41"></path>
          <path d="M2 12h2"></path>
          <path d="M20 12h2"></path>
          <path d="M6 16l1 1"></path>
          <path d="M17 7l1 1"></path>
          <path d="M12 17a5 5 0 0 0 0-10v10z"></path>
        </svg>
      `;
      break;
    case translations.summer:
      iconSvg = `
        <svg class="svg-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <circle cx="12" cy="12" r="5"></circle>
          <path d="M12 1v2"></path>
          <path d="M12 21v2"></path>
          <path d="M4.22 4.22l1.42 1.42"></path>
          <path d="M18.36 18.36l1.42 1.42"></path>
          <path d="M1 12h2"></path>
          <path d="M21 12h2"></path>
          <path d="M4.22 19.78l1.42-1.42"></path>
          <path d="M18.36 5.64l1.42-1.42"></path>
        </svg>
      `;
      break;
    case translations.fall:
      iconSvg = `
        <svg class="svg-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M11 20A7 7 0 0 1 9.8 6.1C15.5 5 17 4.48 19 2c1 2 2 4.18 2 8 0 5.5-4.78 10-10 10Z"></path>
          <path d="M2 21c0-3 1.85-5.36 5.08-6C9 14.32 12 13 13 12"></path>
        </svg>
      `;
      break;
    case translations.winter:
      iconSvg = `
        <svg class="svg-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M2 12h20"></path>
          <path d="M12 2v20"></path>
          <path d="m4.93 4.93 14.14 14.14"></path>
          <path d="m19.07 4.93-14.14 14.14"></path>
        </svg>
      `;
      break;
    default:
      iconSvg = `
        <svg class="svg-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <circle cx="12" cy="12" r="10"></circle>
        </svg>
      `;
  }
  
  return iconSvg;
}

// Create moon visualization
function createMoonVisualization(lunarDay) {
  const moonElement = document.getElementById('moonVisualization');
  moonElement.innerHTML = ''; // Clear existing content
  
  const normalizedLunarDay = ((lunarDay - 1) / 29.53) * 100; // 0-100%
  
  if (normalizedLunarDay < 3 || normalizedLunarDay > 97) {
    // New moon
    const glowElement = document.createElement('div');
    glowElement.className = 'moon-phase light';
    glowElement.style.width = '100%';
    glowElement.style.opacity = '0.1';
    moonElement.appendChild(glowElement);
  } else if (normalizedLunarDay < 50) {
    // Waxing moon - growing from right
    const waxingPercentage = normalizedLunarDay * 2;
    const lightSide = document.createElement('div');
    lightSide.className = 'moon-phase light';
    lightSide.style.right = '0';
    lightSide.style.width = `${waxingPercentage}%`;
    moonElement.appendChild(lightSide);
  } else if (normalizedLunarDay <= 53 && normalizedLunarDay >= 47) {
    // Full moon
    // Add some crater details
    const craters = [
      { size: '1.5rem', top: '1.5rem', left: '2rem', opacity: '0.1' },
      { size: '2rem', top: '5rem', left: '5rem', opacity: '0.1' },
      { size: '1rem', top: '3.5rem', left: '3rem', opacity: '0.1' },
    ];
    
    craters.forEach(crater => {
      const craterElement = document.createElement('div');
      craterElement.style.position = 'absolute';
      craterElement.style.width = crater.size;
      craterElement.style.height = crater.size;
      craterElement.style.borderRadius = '50%';
      craterElement.style.top = crater.top;
      craterElement.style.left = crater.left;
      craterElement.style.background = 'rgba(0,0,0,0.1)';
      craterElement.style.opacity = crater.opacity;
      moonElement.appendChild(craterElement);
    });
  } else {
    // Waning moon - shrinking from left
    const waningPercentage = (normalizedLunarDay - 50) * 2;
    const darkSide = document.createElement('div');
    darkSide.className = 'moon-phase dark';
    darkSide.style.left = '0';
    darkSide.style.width = `${waningPercentage}%`;
    moonElement.appendChild(darkSide);
  }
}

// Update lunar information
function updateLunarInfo() {
  const now = new Date();
  const lunarDayValue = calculateLunarDay(now);
  const lunarPhaseValue = getLunarPhase(lunarDayValue);
  
  document.getElementById('lunarDay').textContent = `${translations.day} ${Math.round(lunarDayValue)}`;
  document.getElementById('lunarPhase').textContent = lunarPhaseValue;
  
  createMoonVisualization(lunarDayValue);
  
  return lunarDayValue;
}

// Calculate current season and days in season
function calculateSeason(date) {
  const year = date.getFullYear();
  
  // Season start dates for Northern Hemisphere (approximate)
  const springEquinox = new Date(year, 2, 20); // March 20
  const summerSolstice = new Date(year, 5, 21); // June 21
  const fallEquinox = new Date(year, 8, 22); // September 22
  const winterSolstice = new Date(year, 11, 21); // December 21
  
  let seasonName = '';
  let seasonStart = null;
  let seasonEnd = null;
  
  if (date >= winterSolstice || date < springEquinox) {
    seasonName = translations.winter;
    seasonStart = date >= winterSolstice ? winterSolstice : new Date(year - 1, 11, 21);
    seasonEnd = springEquinox;
  } else if (date >= springEquinox && date < summerSolstice) {
    seasonName = translations.spring;
    seasonStart = springEquinox;
    seasonEnd = summerSolstice;
  } else if (date >= summerSolstice && date < fallEquinox) {
    seasonName = translations.summer;
    seasonStart = summerSolstice;
    seasonEnd = fallEquinox;
  } else {
    seasonName = translations.fall;
    seasonStart = fallEquinox;
    seasonEnd = winterSolstice;
  }
  
  // Calculate total days in the season
  const seasonLengthMs = seasonEnd.getTime() - seasonStart.getTime();
  const seasonLengthDays = Math.round(seasonLengthMs / (1000 * 60 * 60 * 24));
  
  // Calculate days elapsed in current season
  const daysSinceSeasonStartMs = date.getTime() - seasonStart.getTime();
  const daysSinceSeasonStart = Math.ceil(daysSinceSeasonStartMs / (1000 * 60 * 60 * 24));
  
  // Calculate days remaining in current season
  const daysRemainingInSeason = seasonLengthDays - daysSinceSeasonStart;
  
  return {
    name: seasonName,
    elapsedDays: daysSinceSeasonStart,
    totalDays: seasonLengthDays,
    remainingDays: daysRemainingInSeason
  };
}

// Calculate sun times (sunrise, sunset, day length, night length)
function calculateSunTimes(date, lat, lon, timeZoneOffset) {
  const year = date.getUTCFullYear();
  const month = date.getUTCMonth();
  const day = date.getUTCDate();
  const N = Math.floor((date - new Date(Date.UTC(year, 0, 1))) / (1000 * 60 * 60 * 24)) + 1;

  const δ = 23.45 * Math.sin(2 * Math.PI * (N - 80) / 365);
  const B = 2 * Math.PI * (N - 81) / 365;
  const E = (9.87 * Math.sin(2 * B) - 7.53 * Math.cos(B) - 1.5 * Math.sin(B)) / 60;

  const latRad = lat * Math.PI / 180;
  const δRad = δ * Math.PI / 180;
  const cos_ω = (Math.sin(-0.8333 * Math.PI / 180) - Math.sin(latRad) * Math.sin(δRad)) / 
                (Math.cos(latRad) * Math.cos(δRad));

  if (cos_ω > 1) return { sunrise: { hours: 0, minutes: 0 }, sunset: { hours: 0, minutes: 0 }, dayLength: 0, nightLength: 24 };
  if (cos_ω < -1) return { sunrise: { hours: 0, minutes: 0 }, sunset: { hours: 0, minutes: 0 }, dayLength: 24, nightLength: 0 };

  const ω = Math.acos(cos_ω) * 180 / Math.PI;
  const dayLength = 2 * (ω / 15);
  const nightLength = 24 - dayLength;

  const sunrise_UTC = 12 - (ω / 15) - E - (lon / 15);
  const sunset_UTC = 12 + (ω / 15) - E - (lon / 15);

  const hoursToTime = (hours) => {
    let adjustedHours = hours + timeZoneOffset; // Use passed timeZoneOffset
    while (adjustedHours < 0) adjustedHours += 24;
    while (adjustedHours >= 24) adjustedHours -= 24;
    const h = Math.floor(adjustedHours);
    const m = Math.floor((adjustedHours - h) * 60);
    return { hours: h, minutes: m };
  };

  return {
    sunrise: hoursToTime(sunrise_UTC),
    sunset: hoursToTime(sunset_UTC),
    dayLength: dayLength,
    nightLength: nightLength
  };
}

// Calculate current seasonal hour
function calculateSeasonalHour(now, sunTimes) {
  if (!sunTimes) return null;
  
  const currentHour = now.getHours();
  const currentMinute = now.getMinutes();
  const currentTimeInHours = currentHour + (currentMinute / 60);
  
  // Extract sunrise and sunset times
  const sunriseHour = sunTimes.sunrise.hours;
  const sunriseMinute = sunTimes.sunrise.minutes;
  const sunriseTimeInHours = sunriseHour + (sunriseMinute / 60);
  
  const sunsetHour = sunTimes.sunset.hours;
  const sunsetMinute = sunTimes.sunset.minutes;
  const sunsetTimeInHours = sunsetHour + (sunsetMinute / 60);
  
  // Check if it's day or night
  const isDaytime = (
    (currentTimeInHours >= sunriseTimeInHours && currentTimeInHours < sunsetTimeInHours)
  );
  
  let seasonalHour;
  let hourType;
  
  if (isDaytime) {
    // It's daytime - calculate which of the 12 daylight hours
    // Each daylight hour is (sunset - sunrise) / 12 hours long
    const dayHourLength = sunTimes.dayLength / 12;
    const hoursSinceSunrise = currentTimeInHours - sunriseTimeInHours;
    seasonalHour = Math.floor(hoursSinceSunrise / dayHourLength) + 1;
    hourType = translations.dayHour;
    
    // Calculate how far into this hour (in minutes)
    const hourStart = sunriseTimeInHours + ((seasonalHour - 1) * dayHourLength);
    const minutesIntoHour = Math.floor((currentTimeInHours - hourStart) * 60);
    
    return {
      hourNumber: seasonalHour,
      type: hourType,
      hourLength: dayHourLength * 60, // in minutes
      minutesIntoHour: minutesIntoHour,
      isDaytime: true
    };
  } else {
    // It's nighttime - calculate which of the 12 night hours
    const nightHourLength = sunTimes.nightLength / 12;
    
    // Handle the case when current time is after sunset
    if (currentTimeInHours >= sunsetTimeInHours) {
      const hoursSinceSunset = currentTimeInHours - sunsetTimeInHours;
      seasonalHour = Math.floor(hoursSinceSunset / nightHourLength) + 1;
    } 
    // Handle the case when current time is before sunrise
    else {
      const adjustedTime = currentTimeInHours + 24 - sunsetTimeInHours;
      seasonalHour = Math.floor(adjustedTime / nightHourLength) + 1;
      // If we're past midnight, the calculation might push us to a new night hour
      if (seasonalHour > 12) seasonalHour = 1;
    }
    
    hourType = translations.nightHour;
    
    // Calculate which minute of the hour we're in
    let hourStart;
    if (currentTimeInHours >= sunsetTimeInHours) {
      hourStart = sunsetTimeInHours + ((seasonalHour - 1) * nightHourLength);
    } else {
      // Before sunrise, we need to adjust
      const hoursFromMidnightToSunset = 24 - sunsetTimeInHours;
      const effectiveStart = (seasonalHour - 1) * nightHourLength - hoursFromMidnightToSunset;
      hourStart = effectiveStart >= 0 ? effectiveStart : effectiveStart + 24;
      if (currentTimeInHours < hourStart) hourStart -= 24;
    }
    
    const minutesIntoHour = Math.floor((currentTimeInHours - hourStart) * 60);
    
    return {
      hourNumber: seasonalHour,
      type: hourType,
      hourLength: nightHourLength * 60, // in minutes
      minutesIntoHour: minutesIntoHour >= 0 ? minutesIntoHour : 0,
      isDaytime: false
    };
  }
}

// Format time as HH:MM
function formatTime(hours, minutes) {
  return `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}`;
}

// Format minutes as hours:minutes
function formatHoursMinutes(minutes) {
  const hours = Math.floor(minutes / 60);
  const mins = Math.round(minutes % 60);
  return `${hours}:${String(mins).padStart(2, '0')}`;
}

// Update season information
function updateSeasonInfo() {
  const now = new Date();
  const seasonInfo = calculateSeason(now);
  
  // Update season name and icon
  document.getElementById('seasonName').textContent = seasonInfo.name;
  document.getElementById('seasonIcon').innerHTML = getSeasonIcon(seasonInfo.name);
  
  // Update season progress in three sections
  const progressPercentage = Math.min((seasonInfo.elapsedDays / seasonInfo.totalDays) * 100, 100);
  const section1 = document.getElementById('seasonProgressSection1');
  const section2 = document.getElementById('seasonProgressSection2');
  const section3 = document.getElementById('seasonProgressSection3');
  
  // Calculate progress for each section (each is 33.33% of total width)
  if (progressPercentage <= 33.33) {
    // Only first section is filling
    section1.style.width = `${Math.min(progressPercentage * 3, 100)}%`; // Scale to fill this section (0-100%)
    section2.style.width = '0%';
    section3.style.width = '0%';
  } else if (progressPercentage <= 66.66) {
    // First section full, second section filling
    section1.style.width = '100%';
    section2.style.width = `${Math.min((progressPercentage - 33.33) * 3, 100)}%`; // Scale to fill this section (0-100%)
    section3.style.width = '0%';
  } else {
    // First and second sections full, third section filling
    section1.style.width = '100%';
    section2.style.width = '100%';
    section3.style.width = `${Math.min((progressPercentage - 66.66) * 3, 100)}%`; // Scale to fill this section (0-100%)
  }
  
  // Update season days text
  document.getElementById('seasonDays').textContent = 
    `${translations.day} ${seasonInfo.elapsedDays} ${translations.daysOf} ${seasonInfo.totalDays}`;
  document.getElementById('seasonRemaining').textContent = 
    `${seasonInfo.remainingDays} ${translations.daysRemaining}`;
  
  // Update season card color
  updateSeasonCardColor(seasonInfo.name);
  
  return seasonInfo;
}

// Update seasonal hours information
function updateSeasonalHoursInfo() {
  const hoursLabel = document.querySelector('.hours-label');
  if (!hoursLabel) {
    console.error('hours-label element not found');
    return;
  }
  const now = new Date();
  const timeZoneOffset = -now.getTimezoneOffset() / 60; // Dynamic offset in hours
  currentSunTimes = calculateSunTimes(now, coordinates.latitude, coordinates.longitude, timeZoneOffset);
  currentSeasonalHour = calculateSeasonalHour(now, currentSunTimes);
  if (!currentSunTimes || !currentSeasonalHour) return;
  hoursLabel.textContent = currentSeasonalHour.isDaytime ? 'שעות יום' : 'שעות לילה';
  // Update sunrise/sunset times (if uncommented in HTML)
  document.getElementById('sunriseTime').textContent = 
    formatTime(currentSunTimes.sunrise.hours, currentSunTimes.sunrise.minutes);
  document.getElementById('sunsetTime').textContent = 
    formatTime(currentSunTimes.sunset.hours, currentSunTimes.sunset.minutes);
  
  // Update day/night length (if uncommented in HTML)
  document.getElementById('dayLength').textContent = formatHoursMinutes(currentSunTimes.dayLength * 60);
  document.getElementById('nightLength').textContent = formatHoursMinutes(currentSunTimes.nightLength * 60);
  
  // Update current hour info
  document.getElementById('currentHour').textContent = 
    `${currentSeasonalHour.hourNumber} ${currentSeasonalHour.type}`;
  
  // Update hours label to indicate day or night
  document.querySelector('.hours-label').textContent = 
    currentSeasonalHour.isDaytime ? 'שעות יום' : 'שעות לילה';
  
  // Update hour segments
  const segments = document.querySelectorAll('.hour-segment');
  segments.forEach((segment, index) => {
    const hourNum = index + 1;
    const fill = segment.querySelector('.segment-fill');
    
    if (hourNum < currentSeasonalHour.hourNumber) {
      // Past hours: fully filled
      fill.style.width = '100%';
    } else if (hourNum === currentSeasonalHour.hourNumber) {
      // Current hour: partially filled based on minutes
      const progress = (currentSeasonalHour.minutesIntoHour / currentSeasonalHour.hourLength) * 100;
      fill.style.width = `${progress}%`;
    } else {
      // Future hours: not filled
      fill.style.width = '0%';
    }
  });
  
  // Update hours card color
  updateHoursCardColor();
  
  return currentSeasonalHour;
}

// Update season card color based on current season
function updateSeasonCardColor(seasonName) {
  const seasonCard = document.getElementById('seasonCard');
  
  // Remove all existing season classes
  seasonCard.classList.remove('season-spring', 'season-summer', 'season-fall', 'season-winter');
  
  // Add appropriate season class
  switch (seasonName) {
    case translations.spring:
      seasonCard.classList.add('season-spring');
      break;
    case translations.summer:
      seasonCard.classList.add('season-summer');
      break;
    case translations.fall:
      seasonCard.classList.add('season-fall');
      break;
    case translations.winter:
      seasonCard.classList.add('season-winter');
      break;
  }
  
  // Update class based on dark mode
  if (body.classList.contains('dark-mode')) {
    seasonCard.classList.add('dark-mode');
    seasonCard.classList.remove('light-mode');
  } else {
    seasonCard.classList.add('light-mode');
    seasonCard.classList.remove('dark-mode');
  }
}

// Update hours card color based on day/night status
function updateHoursCardColor() {
  const hoursCard = document.getElementById('hoursCard');
  
  // Remove existing hours classes
  hoursCard.classList.remove('hours-day', 'hours-night');
  
  // Add appropriate hour class
  if (currentSeasonalHour.isDaytime) {
    hoursCard.classList.add('hours-day');
  } else {
    hoursCard.classList.add('hours-night');
  }
  
  // Update class based on dark mode
  if (body.classList.contains('dark-mode')) {
    hoursCard.classList.add('dark-mode');
    hoursCard.classList.remove('light-mode');
  } else {
    hoursCard.classList.add('light-mode');
    hoursCard.classList.remove('dark-mode');
  }
}

// Create background decoration with seasonal elements
function createBackgroundDecoration() {
  const bgContainer = document.getElementById('bgDecoration');
  bgContainer.innerHTML = ''; // Clear existing decorations
  
  // Get current season from season name display
  const currentSeason = document.getElementById('seasonName').textContent;
  
  // Create different decorative elements based on season
  let decorElements = [];
  
  switch (currentSeason) {
    case translations.spring:
      // Spring: flowers and leaves
      decorElements = [
        { size: '12rem', top: '5%', left: '10%', color: '#a3e635', blur: '3rem' },
        { size: '8rem', top: '70%', left: '80%', color: '#d1fae5', blur: '2.5rem' },
        { size: '10rem', top: '40%', left: '5%', color: '#fb7185', blur: '2.8rem' }
      ];
      break;
    case translations.summer:
      // Summer: sun and warm colors
      decorElements = [
        { size: '14rem', top: '10%', left: '75%', color: '#fcd34d', blur: '3.5rem' },
        { size: '9rem', top: '65%', left: '15%', color: '#fdba74', blur: '2.8rem' },
        { size: '7rem', top: '30%', left: '60%', color: '#fca5a5', blur: '2.2rem' }
      ];
      break;
    case translations.fall:
      // Fall: falling leaves and warm earthy tones
      decorElements = [
        { size: '13rem', top: '5%', left: '20%', color: '#f97316', blur: '3rem' },
        { size: '8rem', top: '75%', left: '70%', color: '#b45309', blur: '2.5rem' },
        { size: '10rem', top: '45%', left: '15%', color: '#ef4444', blur: '3rem' }
      ];
      break;
    case translations.winter:
      // Winter: snowflakes and cold colors
      decorElements = [
        { size: '12rem', top: '15%', left: '70%', color: '#93c5fd', blur: '3rem' },
        { size: '9rem', top: '60%', left: '20%', color: '#cbd5e1', blur: '2.5rem' },
        { size: '11rem', top: '35%', left: '65%', color: '#a5b4fc', blur: '2.8rem' }
      ];
      break;
  }
  
  // Add decorative elements to the background
  decorElements.forEach(elem => {
    const bgElement = document.createElement('div');
    bgElement.className = 'bg-element';
    bgElement.style.width = elem.size;
    bgElement.style.height = elem.size;
    bgElement.style.top = elem.top;
    bgElement.style.left = elem.left;
    bgElement.style.backgroundColor = elem.color;
    bgElement.style.filter = `blur(${elem.blur})`;
    bgContainer.appendChild(bgElement);
  });
}

function generateHourSegments() {
  const segmentsContainer = document.getElementById('hourSegments');
  if (!segmentsContainer) {
    console.error('hourSegments container not found');
    return;
  }
  segmentsContainer.innerHTML = ''; // Clear any existing content
  for (let i = 1; i <= 12; i++) {
    const segment = document.createElement('div');
    segment.className = 'hour-segment';
    segment.dataset.hour = i;
    segment.innerHTML = `
      <div class="segment-fill"></div>
      <span class="segment-label">${i}</span>
    `;
    segmentsContainer.appendChild(segment);
  }
}

// Update all visual elements based on current data
function updateAllVisuals() {
  updateDateTime();
  updateLunarInfo();
  updateSeasonInfo();
  updateSeasonalHoursInfo();
  createBackgroundDecoration();
}

// --- GEOLOCATION FUNCTIONALITY ---
const locationToggle = document.getElementById('locationToggle');
const locationDisplay = document.getElementById('locationDisplay');

locationToggle.addEventListener('click', getLocation);

function getLocation() {
  locationDisplay.textContent = translations.locating;
  locationDisplay.hidden = false;

  if ("geolocation" in navigator) {
    navigator.geolocation.getCurrentPosition(success, error, {
      enableHighAccuracy: true,
      timeout: 5000
    });
  } else {
    locationDisplay.textContent = translations.notSupported;
  }

  function success(position) {
    coordinates.latitude = position.coords.latitude;
    coordinates.longitude = position.coords.longitude;
    
    // Once we have a new location, update the URL
    const newUrl = new URL(window.location.href);
    newUrl.searchParams.set('lat', coordinates.latitude);
    newUrl.searchParams.set('lon', coordinates.longitude);
    window.history.pushState({}, '', newUrl);

    // Update all visuals with the new coordinates
    updateAllVisuals();
    
    // Display a success message
    locationDisplay.textContent = `${translations.coordinates} ${coordinates.latitude.toFixed(4)}, ${coordinates.longitude.toFixed(4)}`;
  }

  function error(err) {
    let errorMessage = translations.permissionDenied;
    switch (err.code) {
      case err.POSITION_UNAVAILABLE:
        errorMessage = translations.positionUnavailable;
        break;
      case err.TIMEOUT:
        errorMessage = translations.requestTimeout;
        break;
    }
    locationDisplay.textContent = `שגיאה: ${errorMessage}`;
  }
}

// --- INITIALIZATION ---
document.addEventListener('DOMContentLoaded', () => {
  generateHourSegments();
  updateAllVisuals();
});

// Update every minute
setInterval(updateAllVisuals, 60000);