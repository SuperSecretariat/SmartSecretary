export class NavBarItem {
    private name: string;
    private route: string;

    constructor(name: string, route: string) {
        this.name = name;
        this.route = route;
    }

    getName (): string {
        return this.name;
    }

    getRoute (): string {
        return this.route;
    }
}