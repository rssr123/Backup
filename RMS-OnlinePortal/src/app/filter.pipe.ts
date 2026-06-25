import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'filter'
})
export class FilterPipe implements PipeTransform {
  transform(items: any[], searchText: string): any[] {
    if (!items) return [];
    if (!searchText) return items;

    searchText = searchText.toLowerCase();
    return items.filter(item => {
      // Verify that these keys exist in your API response!
      const matches = (item.entity_no && item.entity_no.toLowerCase().includes(searchText));
      //console.log('Filtering item:', item, 'matches:', matches);
      return matches;
    });
  }
}
