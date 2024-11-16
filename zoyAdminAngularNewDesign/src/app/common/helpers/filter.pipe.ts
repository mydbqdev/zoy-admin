import { Pipe, PipeTransform } from '@angular/core';
import { CommonModule } from "@angular/common";

@Pipe({ name: 'appFilter' })
export class PerformancePipe implements PipeTransform {
  /**
   * Pipe filters the list of elements based on the search text provided
   *
   * @param items list of elements to search in
   * @param searchText search string
   * @returns list of elements filtered by search text or []
   */
  transform(items: any[], searchYear: string): any[] {
    if (!items) {
      return [];
    }
    if (!searchYear) {
      return items;
    }
    searchYear = searchYear.toLocaleLowerCase();

    return items.filter(it => {
      if (it[1].toLocaleLowerCase() == (searchYear.toLocaleLowerCase())) {

        return true;
      }
      else { return it.includes(searchYear); }

    });
  }

}