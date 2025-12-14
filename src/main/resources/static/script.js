class MatchPredictor {
    constructor() {
        this.apiBaseUrl = 'http://localhost:8080';
        this.homeTeamSelect = document.getElementById('homeTeam');
        this.awayTeamSelect = document.getElementById('awayTeam');
        this.predictBtn = document.getElementById('predictBtn');
        this.loadingElement = document.getElementById('loading');
        this.matchesResultElement = document.getElementById('matchesResult');
        this.resultElement = document.getElementById('result');
        this.errorElement = document.getElementById('error');

         this.homeTeamMatchesElement = document.getElementById('homeTeamMatches');
         this.awayTeamMatchesElement = document.getElementById('awayTeamMatches');
         this.homeTeamMatchesTitle = document.getElementById('homeTeamMatchesTitle');
         this.awayTeamMatchesTitle = document.getElementById('awayTeamMatchesTitle');

        this.init();
    }

    init() {
        this.loadTeams();
        this.setupEventListeners();
    }

    async loadTeams() {
        try {
            const response = await fetch(`${this.apiBaseUrl}/teams`);
            const teams = await response.json();

            this.populateTeamSelects(teams);
            this.initializeSelect2();
        } catch (error) {
            console.error('Ошибка загрузки команд:', error);
            this.populateTeamSelects(['Реал Мадрид', 'Барселона', 'Атлетико Мадрид', 'Севилья']);
        }
    }

    populateTeamSelects(teams) {
        const teamNames = teams.map(team => team.name);

        this.homeTeamSelect.innerHTML = '';
        this.awayTeamSelect.innerHTML = '';

        const emptyOption1 = new Option('Выберите команду', '');
        const emptyOption2 = new Option('Выберите команду', '');
        this.homeTeamSelect.appendChild(emptyOption1);
        this.awayTeamSelect.appendChild(emptyOption2);

        teamNames.forEach(team => {
            const option1 = new Option(team, team);
            const option2 = new Option(team, team);
            this.homeTeamSelect.appendChild(option1);
            this.awayTeamSelect.appendChild(option2);
        })
    }

    initializeSelect2() {
            $(this.homeTeamSelect).select2({
                placeholder: "Начните вводить название команды",
                language: "ru",
                width: '100%',
                minimumResultsForSearch: 1
            });

            $(this.awayTeamSelect).select2({
                placeholder: "Начните вводить название команды",
                language: "ru",
                width: '100%',
                minimumResultsForSearch: 1
            });
        }

    setupEventListeners() {
        this.predictBtn.addEventListener('click', () => this.getPrediction());
    }

    async getPrediction() {
        const homeTeam = $(this.homeTeamSelect).val();
        const awayTeam = $(this.awayTeamSelect).val();
        const matchCount = document.getElementById('matchCount').value;

        if (!homeTeam || !awayTeam) {
            alert('Пожалуйста, выберите обе команды');
            return;
        }

        if (homeTeam === awayTeam) {
            alert('Команды не могут быть одинаковыми');
            return;
        }

        this.showLoading();
        this.hideMatchesResult();
        this.hideResult();
        this.hideError();

        try {
            const [homeMatches, awayMatches, prediction] = await Promise.all([
            this.fetchRecentMatches(homeTeam, matchCount),
            this.fetchRecentMatches(awayTeam, matchCount),
            this.fetchPrediction(homeTeam, awayTeam, matchCount)
            ]);

            this.displayRecentMatches(homeTeam, awayTeam, homeMatches, awayMatches);
            this.displayPrediction(prediction);
        } catch (error) {
             console.error('Ошибка:', error);
             this.showError();
        } finally {
              this.hideLoading();
        }
    }

    async fetchRecentMatches(teamName, countLastMatches) {
        const url = `${this.apiBaseUrl}/match?teamName=${encodeURIComponent(teamName)}&countLastMatches=${countLastMatches}`;

        const response = await fetch(url);

        if (!response.ok) {
            return [];
        }

        return await response.json();
    }

    async fetchPrediction(homeTeam, awayTeam, countLastMatches) {
        const url = `${this.apiBaseUrl}/match-predict?homeTeam=${encodeURIComponent(homeTeam)}&awayTeam=${encodeURIComponent(awayTeam)}&countLastMatches=${countLastMatches}`;

        const response = await fetch(url);

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return await response.json();
    }

    displayRecentMatches(homeTeam, awayTeam, homeMatches, awayMatches) {
        this.homeTeamMatchesTitle.textContent = `${homeTeam} (последние ${homeMatches.length} матчей)`;
        this.awayTeamMatchesTitle.textContent = `${awayTeam} (последние ${awayMatches.length} матчей)`;

        this.displayMatchesList(this.homeTeamMatchesElement, homeMatches, homeTeam);
        this.displayMatchesList(this.awayTeamMatchesElement, awayMatches, awayTeam);

        this.showMatchesResult();
    }

    displayMatchesList(container, matches, currentTeam) {
        container.innerHTML = '';

        if (!matches || matches.length === 0) {
            container.innerHTML = '<p class="no-matches">Нет данных о матчах</p>';
            return;
        }

        matches.forEach(match => {
            const matchElement = document.createElement('div');
            matchElement.className = 'match-item';

            const isHome = match.homeTeam === currentTeam;
            const resultClass = this.getMatchResultClass(match, currentTeam);
            const resultText = this.getMatchResultText(match, currentTeam);

            const date = new Date(match.matchDate).toLocaleDateString('ru-RU', {
                day: '2-digit',
                month: '2-digit',
                year: 'numeric'
            });

            matchElement.innerHTML = `
                <div class="match-header">
                    <span class="match-date">${date}</span>
                    <span class="match-tournament">${match.tournament || 'Турнир'}</span>
                </div>
                <div class="match-teams">
                    <span class="team-name ${isHome ? 'current-team' : ''}">${match.homeTeam}</span>
                    <span class="match-score ${resultClass}">
                        ${match.homeTeamGoals} - ${match.awayTeamGoals}
                    </span>
                    <span class="team-name ${!isHome ? 'current-team' : ''}">${match.awayTeam}</span>
                </div>
                <div class="match-info">
                    <span class="match-location">
                        ${isHome ? 'Дома' : 'В гостях'}
                    </span>
                    <span class="match-result ${resultClass}">
                        ${resultText}
                    </span>
                </div>
            `;

            container.appendChild(matchElement);
        });
    }

    getMatchResultClass(match, currentTeam) {
        const isHome = match.homeTeam === currentTeam;
        const currentGoals = isHome ? match.homeTeamGoals : match.awayTeamGoals;
        const opponentGoals = isHome ? match.awayTeamGoals : match.homeTeamGoals;

        if (currentGoals > opponentGoals) return 'win';
        if (currentGoals < opponentGoals) return 'lose';
        return 'draw';
    }

    getMatchResultText(match, currentTeam) {
        const isHome = match.homeTeam === currentTeam;
        const currentGoals = isHome ? match.homeTeamGoals : match.awayTeamGoals;
        const opponentGoals = isHome ? match.awayTeamGoals : match.homeTeamGoals;

        if (currentGoals > opponentGoals) return 'Победа';
        if (currentGoals < opponentGoals) return 'Поражение';
        return 'Ничья';
    }

    displayPrediction(prediction) {
        document.getElementById('homeWinPercent').textContent = this.formatPercent(prediction.homeWinChance);
        document.getElementById('drawPercent').textContent = this.formatPercent(prediction.drawChance);
        document.getElementById('awayWinPercent').textContent = this.formatPercent(prediction.awayWinChance);

        if (prediction.homeExpectedGoals && prediction.awayExpectedGoals) {
            document.getElementById('expectedHomeGoals').textContent = prediction.homeExpectedGoals.toFixed(2);
            document.getElementById('expectedAwayGoals').textContent = prediction.awayExpectedGoals.toFixed(2);
        }

        if (prediction.scoreProbabilities) {
            this.displayScoreProbabilities(prediction.scoreProbabilities);
        }

        this.showResult();
    }

    displayScoreProbabilities(scoreProbabilities) {
        const scoreTable = document.getElementById('scoreTable');
        scoreTable.innerHTML = '';

        const sortedScores = Object.entries(scoreProbabilities)
            .sort(([,a], [,b]) => b - a)
            .slice(0, 12);

        sortedScores.forEach(([score, probability]) => {
            const scoreItem = document.createElement('div');
            scoreItem.className = 'score-item';
            scoreItem.innerHTML = `
                <div class="score">${score}</div>
                <div class="prob">${this.formatPercent(probability)}</div>
            `;
            scoreTable.appendChild(scoreItem);
        });
    }

    formatPercent(value) {
        return `${(value * 100).toFixed(1)}%`;
    }

    showLoading() {
        this.loadingElement.classList.remove('hidden');
    }

    hideLoading() {
        this.loadingElement.classList.add('hidden');
    }

    showResult() {
        this.resultElement.classList.remove('hidden');
    }

    hideResult() {
        this.resultElement.classList.add('hidden');
    }

    showMatchesResult() {
        this.matchesResultElement.classList.remove('hidden');
    }

    hideMatchesResult() {
         this.matchesResultElement.classList.add('hidden');
    }

    showError() {
        this.errorElement.classList.remove('hidden');
    }

    hideError() {
        this.errorElement.classList.add('hidden');
    }
}

document.addEventListener('DOMContentLoaded', () => {
    new MatchPredictor();
});